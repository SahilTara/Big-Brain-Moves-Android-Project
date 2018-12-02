package com.uottawa.bigbrainmoves.servio.repositories;

import android.util.Patterns;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.enums.SignupResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

public class DbHandler implements Repository {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AvailabilitiesRepository availabilitiesRepository = new AvailabilitiesRepositoryFirebase();
    private static final String USERNAME_TO_EMAIL = "user_ids";
    private static final String ACCOUNT_INFO = "user_info";
    private static final String SERVICE_TYPES = "service_types";
    private static final String PROVIDED_SERVICES = "currently_provided";
    private static final String ADMIN_EXISTS = "admin_exists";
    private static final String BOOKINGS = "bookings";

    /*
    Begin Account/User related Methods.
     */
    /**
     * Method to get the email for a given username from the database.
     *
     * @param username username to get the email for.
     * @return RxJava Observable containing the success status of the login.
     */
    private Observable<Boolean> loginWithUsername(final String username, final String password) {
        return Observable.create(subscriber ->
                myRef.child(USERNAME_TO_EMAIL).child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String emailId = "";
                        if (subscriber.isDisposed())
                            return;
                        /*
                           makes it so empty string email id handles the failure cases. Since
                           email can never be the empty string.
                         */
                        if (dataSnapshot.exists()) {
                            emailId = dataSnapshot.getValue(String.class);
                        }

                        if (emailId == null) {
                            emailId = "";
                        }

                        subscriber.onNext(emailId);
                    }

                    @Override // database permission error
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                })).flatMap(result -> {
            /*
             * We use a flat map to chain in another observer, this is very useful since this allows us
             * to not have nested observers. Remember "Observer-ception" is bad! This type of style,
             * is incorporated throughout this class, and should be remembered and preferred over the
             * nested observer style.
             *
             * We return false here if the emailId is empty, that is it is "".
             */
           if (result instanceof String && !result.equals("")) {
               return loginToAccount((String)result, password);
           } else {
                return Observable.create(subscriber -> {
                   subscriber.onNext(false);
                   subscriber.onComplete();
                });
           }
        });

    }

    /**
     * Private helper method to login to the account with the email and password of user.
     * Used to simplify the logic of the application, simply returns true to the observer if
     * successful login, false otherwise.
     * @param email email to attempt login with.
     * @param password password to attempt login with.
     * @return rx observable containing the success status of the login.
     */
    private Observable<Boolean> loginToAccount(String email, String password) {
        return Observable.create(subscriber -> mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success

                        subscriber.onNext(true);
                        subscriber.onComplete();
                    } else { // otherwise sign in failed.
                        subscriber.onNext(false);
                        subscriber.onComplete();
                    }
                }));
    }
    /**
     * Attempts to verify user validity, and logs in if valid.
     * Returns the success status of the login.
     *
     * @param input    The email or username of the user
     * @param password the password of the user
     * @return RxJava observable containing the success status of the login.
     */
    public Observable<Boolean> login(String input, String password) {
        // Check if the input isn't an email, and if it isn't we get the email from the username.
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            return loginWithUsername(input, password);
        } else {
            return loginToAccount(input, password);
        }


    }


    /**
     * Method returns a list of all accounts currently in the database to the observers.
     * @return a rx observable containing a list of all accounts in the database.
     */
    public Observable<List<Account>> getAllUsersFromDataBase() {
        return Observable.create(subscriber ->
                myRef.child(ACCOUNT_INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (subscriber.isDisposed())
                            return;
                        // We have some users in the database.
                        getAllGenericOnDataChange(dataSnapshot, subscriber, Account.class);
                        /*
                        if (dataSnapshot.exists()) {
                            ArrayList<Account> accounts = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Account account = snapshot.getValue(Account.class);
                                if (account != null) {
                                    accounts.add(account);
                                }
                            }
                            subscriber.onNext(accounts);
                            subscriber.onComplete();
                        }
                        */
                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));

    }


    /**
     * Gets an Account Object from a FireBase base id, and returns an observable using that id.
     * @param uid the uid of the FireBase user
     * @return an rxJava observable of the account.
     */
    public Observable<Optional<Account>> getUserFromDataBase(String uid) {
        return Observable.create(subscriber ->
                myRef.child(ACCOUNT_INFO).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // if the subscriber is disposed we don't care about result
                            if (subscriber.isDisposed())
                                return;

                            Account account = dataSnapshot.getValue(Account.class);

                            if (account.getType().equals(AccountType.SERVICE_PROVIDER)) {
                                account = dataSnapshot.getValue(ServiceProvider.class);
                            }

                            Optional<Account> accountOptional = Optional.ofNullable(account);

                            subscriber.onNext(accountOptional);
                            subscriber.onComplete();
                        } else {
                            subscriber.onNext(Optional.empty());
                            subscriber.onComplete();
                        }
                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        // if the subscriber is disposed we don't care about result
                        if (subscriber.isDisposed())
                            return;

                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));
    }

    // TODO DON'T REPEAT YOUR SELF FIX LATER!
    /**
     * Gets an Account object of type ServiceProvider, and returns an observable using that username.
     * If the user is non existent / isn't the correct type an empty optional is returned.
     * @param username the username of the service provider account.
     * @return an rxJava observable of the account.
     */
    public Observable<Optional<ServiceProvider>> getServiceProviderFromDatabase(String username) {
        return Observable.create(subscriber ->
                myRef.child(ACCOUNT_INFO).orderByChild("username").equalTo(username).limitToFirst(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            //
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() == 1) {
                                    // if the subscriber is disposed we don't care about result
                                    if (subscriber.isDisposed())
                                        return;
                                        // should only run once.
                                        dataSnapshot = dataSnapshot.getChildren().iterator().next();

                                        Account account = dataSnapshot.getValue(Account.class);
                                        ServiceProvider provider = null;

                                        if (account.getType().equals(AccountType.SERVICE_PROVIDER)) {
                                            provider = dataSnapshot.getValue(ServiceProvider.class);
                                        }

                                        Optional<ServiceProvider> providerOptional = Optional.ofNullable(provider);

                                        subscriber.onNext(providerOptional);
                                        subscriber.onComplete();

                                } else {
                                    subscriber.onNext(Optional.empty());
                                    subscriber.onComplete();
                                }
                            }

                            @Override // Only really called when the database doesn't give enough permissions.
                            public void onCancelled(@NonNull DatabaseError dbError) {
                                // if the subscriber is disposed we don't care about result
                                if (subscriber.isDisposed())
                                    return;

                                subscriber.onError(new FirebaseException(dbError.getMessage()));
                            }
                        }));
    }

    /**
     * Attempts to create the physical FireBase user account,
     * @param email the email of the account to create
     * @param password the password of the account to create
     * @param displayName the display name of the account to create.
     * @param type the type of the account (Service, Home, Admin)
     * @return RxJava observable containing the sign up result.
     */
    private Observable<SignupResult> createAccount(final String email,
                                                   final String username,
                                                   final String password,
                                                   final String displayName,
                                                   final AccountType type) {
        return Observable.create(subscriber -> mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in account's information
                        Account account;

                        if (type.equals(AccountType.SERVICE_PROVIDER)) {
                            // Service Provider class polymorphically created.
                            account = new ServiceProvider(displayName, username,
                                    "", "", "",
                                    "", false);
                        } else {
                            account = new Account(displayName, type, username);
                        }

                        writeUserNameToDatabase(username, email);
                        writeUserToDataBase(getUserId(), account);
                        subscriber.onNext(SignupResult.ACCOUNT_CREATED);
                        subscriber.onComplete();
                    } else {
                        subscriber.onNext(SignupResult.EMAIL_TAKEN);
                        subscriber.onComplete();
                    }
                }));
    }

    /**
     * Adds the displayName, type to the database under the uid.
     *
     */
    private void writeUserToDataBase(String uid, Account account) {
        myRef.child(ACCOUNT_INFO).child(uid).setValue(account);
        if (account.getType().equals(AccountType.ADMIN)) {
            // If we write admin type to database we have to do some things to make sure no more admins are made.
            myRef.child(ADMIN_EXISTS).setValue(true);
        }
    }

    /**
     * Writes the username : email to database for username login.
     * @param username username specified by the user.
     * @param email email specified by the user.
     */
    private void writeUserNameToDatabase(String username, String email) {
        myRef.child(USERNAME_TO_EMAIL).child(username).setValue(email);
    }

    /**
     * Method to check if an admin account exists.
     * @return the existence of an admin account.
     */
    public Observable<Boolean> doesAdminAccountExist() {

        return Observable.create(subscriber ->
                myRef.child(ADMIN_EXISTS).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (subscriber.isDisposed())
                            return;
                        // admin exists!
                        Boolean bool = false;
                        if (dataSnapshot.exists()) {
                            bool = dataSnapshot.getValue(Boolean.class);
                        }

                        subscriber.onNext(bool);
                        subscriber.onComplete();
                    }

                    @Override // usually due to insufficient permissions
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));
    }

    /**
     * Checks if the user exists, if not adds to db.
     * @param username the username to check.
     * @param email the email of user
     * @param password the password of the user.
     * @param displayName the display name of the user.
     * @param typeSelected the type of the user.
     */
    public Observable<SignupResult> createUserIfNotInDataBase(final String email,
                                                              final String username,
                                                              final String password,
                                                              final String displayName,
                                                              final AccountType typeSelected) {
        return Observable.create(subscriber -> {
            // We make sure that a user does not exist with this username.
            // We will return true if the user exists, false otherwise in this observer.
            myRef.child(USERNAME_TO_EMAIL).child(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (subscriber.isDisposed())
                                return;

                            subscriber.onNext(dataSnapshot.exists());
                        }

                        @Override // Some kind of error
                        public void onCancelled(@NonNull DatabaseError dbError) {
                            if (subscriber.isDisposed())
                                return;
                            subscriber.onError(new FirebaseException(dbError.getMessage()));
                        }
                    });
        }).flatMap(result -> {
            // if the result is equal to false then
            if (result instanceof Boolean && result.equals(false)) {
                return createAccount(email, username, password, displayName, typeSelected);
            } else {
                return Observable.create(subscriber -> {
                    subscriber.onNext(SignupResult.USERNAME_TAKEN);
                    subscriber.onComplete();
                });
            }
        });
    }





    /**
     * Check if a user is currently logged in to FireBase.
     * @return True if a user is logged in, false otherwise.
     */
    public boolean checkIfUserIsLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * If logged in this method returns the uid of the FireBase user,
     * Otherwise null.
     * @return String mapping to the FireBase user's uid or null if no logged in user.
     */
    public String getUserId() {
        if(mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }

    /**
     * Signs out the current FireBase user.
     */
    public void signOutCurrentUser() {
        if (getUserId() != null) {
            mAuth.signOut();
        }
    }


    /*
    End Account/User related Methods.
     */

    /*
    Begin Service Related Methods
     */

    /**
     * Creates a service type if it doesn't already exist. Returns whether or not the service type was
     * successfully created.
     * @param serviceTypeName service type name to create
     * @param value rate that the service type will go for.
     * @return RxJava observable containing whether or not the service type was successfully created.
     */
    public Observable<Boolean> createServiceTypeIfNotInDatabase(String serviceTypeName, double value) {
        return Observable.create(subscriber -> {
            // We make sure that a user does not exist with this uid.
            myRef.child(SERVICE_TYPES).child(serviceTypeName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (subscriber.isDisposed())
                                return;
                            if (!dataSnapshot.exists()) {
                                writeServiceTypeToDatabase(serviceTypeName, value);
                                changeOfferingStatusOfAllServicesWithType(serviceTypeName, true);
                                subscriber.onNext(true);
                                subscriber.onComplete();
                            } else {
                                subscriber.onNext(false);
                                subscriber.onComplete();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError dbError) {
                            if (subscriber.isDisposed())
                                return;
                            subscriber.onError(new FirebaseException(dbError.getMessage()));
                        }
                    });
        });
    }

    /**
     * Writes the service type to the database
     * @param serviceTypeName name of the service type.
     * @param value the rate of the service type.
     */
    private void writeServiceTypeToDatabase(String serviceTypeName, double value) {
        ServiceType serviceType = new ServiceType(serviceTypeName, value);
        myRef.child(SERVICE_TYPES).child(serviceTypeName).setValue(serviceType);
    }



    /**
     * Method listens for changes in the service types database, and notifies all observers.
     * @return RxJava containing a service type and whether removed or not.
     */
    public Observable<Pair<ServiceType, Boolean>> listenForServiceTypeChanges() {
        return Observable.create(subscriber ->
                myRef.child(SERVICE_TYPES).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ServiceType serviceType = dataSnapshot.getValue(ServiceType.class);
                        subscriber.onNext(new Pair<>(serviceType, false));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ServiceType serviceType = dataSnapshot.getValue(ServiceType.class);
                        subscriber.onNext(new Pair<>(serviceType, false));
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        ServiceType serviceType = dataSnapshot.getValue(ServiceType.class);
                        subscriber.onNext(new Pair<>(serviceType, true));
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        subscriber.onError(new FirebaseException(databaseError.getMessage()));
                    }
        }));
    }

    /**
     * Method listens for changes in services in the database, and notifies all observers.
     * @return RxJava containing a service and whether removed or not.
     */
    public Observable<Pair<Service, Boolean>> listenForServiceChanges() {
        return Observable.create(subscriber ->
                myRef.child(PROVIDED_SERVICES).orderByChild("offeredDisabled").equalTo("truefalse")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Service service = dataSnapshot.getValue(Service.class);
                subscriber.onNext(new Pair<>(service, false));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Service service = dataSnapshot.getValue(Service.class);
                subscriber.onNext(new Pair<>(service, false));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Service service = dataSnapshot.getValue(Service.class);
                subscriber.onNext(new Pair<>(service, true));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subscriber.onError(new FirebaseException(databaseError.getMessage()));
            }
        }));
    }

    /**
     * Deletes the specified service type from the database.
     * @param serviceTypeName name of service type to delete
     */
    public void deleteServiceType(String serviceTypeName) {
        myRef.child(SERVICE_TYPES).child(serviceTypeName).removeValue();
        changeOfferingStatusOfAllServicesWithType(serviceTypeName, false);
    }

    /**
     * Edits the value(rate) of the service type specified
     * @param serviceTypeName name of service type to edit
     * @param value the new rate to set to.
     */
    public void editServiceType(String serviceTypeName, double value) {
        writeServiceTypeToDatabase(serviceTypeName, value);
    }

    /**
     * Method simply returns all service types within an rxjava observable.
     * @return rxjava observable containing all current service types.
     */
    private Observable<List<ServiceType>> getAllServiceTypes() {
        return Observable.create(subscriber ->
                myRef.child(SERVICE_TYPES).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        getAllGenericOnDataChange(dataSnapshot, subscriber, ServiceType.class);
                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
        }));
    }

    /**
     * Method simply returns an optional ervice type with the matching string within an rxjava observable.
     * @param serviceTypeName name of the service type you would like to attempt to retrieve
     * @return an rxjava observable of optional of service type.
     */
    public Observable<Optional<ServiceType>> getServiceType(String serviceTypeName) {
        return Observable.create(subscriber ->
                myRef.child(SERVICE_TYPES).child(serviceTypeName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (subscriber.isDisposed())
                            return;

                        ServiceType serviceType = null;

                        if (dataSnapshot.exists()) {
                             serviceType = dataSnapshot.getValue(ServiceType.class);
                        }

                        subscriber.onNext(Optional.ofNullable(serviceType));
                        subscriber.onComplete();
                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));
    }


    //TODO Rework comments here.
    /**
     * Generic on data change method for getting all instances of a certain type from the database.
     * @param dataSnapshot firebase datasnapshot of the results where the instances live.
     * @param subscriber the rxjava emitter.
     * @param <K> The type of the data.
     */
    private <K> void getAllGenericOnDataChange(DataSnapshot dataSnapshot,
                                               ObservableEmitter<List<K>> subscriber,
                                               Class<K> classToReturn) {
        // TODO if stable remove comment.
        /*
        if (subscriber.isDisposed())
            return;
        // We have some services in the database for the user
        if (dataSnapshot.exists()) {
            ArrayList<K> elements = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                GenericTypeIndicator<K> indicator = new GenericTypeIndicator<K>(){};

                K element = snapshot.getValue(indicator);
                if (element != null) {
                    elements.add(element);
                }
            }


            subscriber.onNext(elements);
            subscriber.onComplete();
        } else {
            // deal with no services case
            subscriber.onNext(Collections.emptyList());
            subscriber.onComplete();
        } */


        // default call to the main getAllGenericOnDataChange method.
        getAllGenericOnDataChange(dataSnapshot, subscriber, classToReturn, List::add, (elements, subber) -> {
            subber.onNext(elements);
            subber.onComplete();
        }, (subber) -> {
            subber.onNext(new ArrayList<>());
            subber.onComplete();
        });
    }

    // Used to allow for less code duplication.
    private <K> void getAllGenericOnDataChange(DataSnapshot dataSnapshot,
                                               ObservableEmitter<List<K>> subscriber,
                                               Class<K> classTypeToReturn,
                                               BiConsumer<List<K>, K> mutator,
                                               BiConsumer<List<K>, ObservableEmitter<List<K>>> postLoopAction,
                                               Consumer<ObservableEmitter<List<K>>> snapshotDoesNotExist) {

        if (subscriber.isDisposed())
            return;


        // We have some elements in the database
        if (dataSnapshot.exists()) {
            ArrayList<K> elements = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                K element = snapshot.getValue(classTypeToReturn);
                if (element != null) {
                    try {
                        // execute our mutator hook method.
                        mutator.accept(elements, element);
                    } catch (Exception e) {
                        // Can't really do anything else since exception could be anything
                        subscriber.onError(e);
                    }
                }
            }

            try {
                // call the post action hook.
                postLoopAction.accept(elements, subscriber);
            } catch (Exception e) {
                // Can't really do anything else since exception could be anything
                subscriber.onError(e);
            }

        } else {
            // deal with no elements case by calling the snapshotDoesNotExist hook.
            try {
                snapshotDoesNotExist.accept(subscriber);
            } catch (Exception e) {
                // Cannot do anything here
                subscriber.onError(e);
            }
        }


    }




    /**
     * Gets all active services provided by the current user in the database.
     * @return rxjava observable containing all active services of user.
     */
    @Override
    public Observable<List<Service>> getServicesProvidedByCurrentUser() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String username = account.getUsername();

        return Observable.create(subscriber ->
                myRef.child(PROVIDED_SERVICES).orderByChild("usernameOfferedDisabled")
                .equalTo(username + String.valueOf(true) + false).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        getAllGenericOnDataChange(dataSnapshot, subscriber, Service.class);
                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));
    }


    /**
     * Method filters the service type names based on the names of the services already provided,
     * to get the service object this it is then converted to physical service objects by retrieving
     * old services dropped, or creating completely new objects which is then returned as an rxjava observable.
     * @param provided the names of the services already provided
     * @return rxjava observable containing services which are providable.
     */
    @Override
    public Observable<List<Service>> getServicesProvidable(List<String> provided) {
        return getAllServiceTypes().flatMapIterable(serviceTypes -> serviceTypes) // turns into an iterable stream
                .filter(item->!provided.contains(item.getType()))    // filters based on contains
                .map(ServiceType::getType)                           // converts service type to the type string.
                .toList().flatMapObservable(this::getOldServicesOrDefault); // gets list of service objects
    }

    /**
     * Method gets old services dropped if they exist based on the serviceTypes and for the ones which
     * don't have a dropped object a new one is created and returned in an rxjava observable.
     * @param serviceTypes names of serviceTypes providable
     * @return rxjava object containing physical services providable by the service provider.
     */
    private Observable<List<Service>> getOldServicesOrDefault(List<String> serviceTypes) {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String username = account.getUsername();
        String displayName = account.getDisplayName();

        return Observable.create(subscriber ->
                myRef.child(PROVIDED_SERVICES).orderByChild("usernameOfferedDisabled")
                .equalTo(username + String.valueOf(false) + false).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // TODO if stable remove comment
                        /*
                        if (subscriber.isDisposed())
                            return;

                        // We have some services previously provided
                        if (dataSnapshot.exists()) {
                            ArrayList<Service> services = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Service service = snapshot.getValue(Service.class);
                                if (service != null) {
                                    if (serviceTypes.contains(service.getType())) {
                                        services.add(service);
                                        serviceTypes.remove(service.getType());
                                    }
                                }
                            }
                            // Deal with the remaining services that don't exist yet.
                            for (String serviceType : serviceTypes) {
                                services.add(new Service(serviceType, username, displayName, 0,
                                        0.0, false, false));
                            }


                            subscriber.onNext(services);
                            subscriber.onComplete();
                        } else {
                            // deal with no services case
                            ArrayList<Service> services = new ArrayList<>();
                            for (String serviceType : serviceTypes) {
                                services.add(new Service(serviceType, username, displayName, 0,
                                        0.0, false, false));
                            }
                            subscriber.onNext(services);
                            subscriber.onComplete();

                        }
                        */

                        getAllGenericOnDataChange(dataSnapshot, subscriber, Service.class, (services, service) -> {
                            /* if old services exist in the database and they are in the services providable
                             * by the user then we execute this.
                             */
                            if (serviceTypes.contains(service.getType())) {
                                services.add(service);
                                serviceTypes.remove(service.getType());
                            }
                        }, (services, subber) -> {
                            // Deal with the remaining services that don't exist yet.
                            for (String serviceType : serviceTypes) {
                                services.add(new Service(serviceType, username, displayName, 0,
                                        0.0, false, false));
                            }
                            subber.onNext(services);
                            subber.onComplete();
                        } , (subber) -> {
                            // deal with no services case
                            ArrayList<Service> services = new ArrayList<>();
                            for (String serviceType : serviceTypes) {
                                services.add(new Service(serviceType, username, displayName, 0,
                                        0.0, false, false));
                            }
                            subber.onNext(services);
                            subber.onComplete();
                        });

                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));
    }

    /**
     * Method saves the profile of a service provider and returns if it was successful or not
     * @param phoneNumber updated phone number of service provider
     * @param address updated address of service provider
     * @param companyName updated company name of the service provider
     * @param description updated description of the service provider
     * @param isLicensed whether or not the service provider is a licensed professional.
     * @param modified the services which were either added or removed to the service provider's offered
     *                 services.
     * @return rxjava boolean observable that holds the state of whether the save was successful or not.
     */
    @Override
    public Observable<Boolean> saveProfile(String phoneNumber,
                                           String address, String companyName,
                                           String description, boolean isLicensed,
                                           List<Service> modified) {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String displayName = account.getDisplayName();
        String username = account.getUsername();

        HashMap<String, Object> updateMap = new HashMap<>();

        // specify the services to be updated
        for (Service service : modified) {
            updateMap.put(PROVIDED_SERVICES + "/" + service.getType() + username, service);
        }

        // specify the user to be updated
        updateMap.put(ACCOUNT_INFO + "/" + getUserId(),
                new ServiceProvider(displayName, username, phoneNumber, address, companyName,
                        description, isLicensed));

        return Observable.create(subscriber ->
                myRef.updateChildren(updateMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // successful update
                        subscriber.onNext(true);
                        subscriber.onComplete();
                    } else { // otherwise unsuccessful
                        subscriber.onNext(false);
                        subscriber.onComplete();
                    }
                }));
    }

    /**
     * changes offering status of services with specified type name if such exist.
     */

    private void changeOfferingStatusOfAllServicesWithType(String serviceType, boolean isOffered) {
        myRef.child(PROVIDED_SERVICES).orderByChild("typeDisabled").equalTo(serviceType + isOffered)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Object> updateMap = new HashMap<>();
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Service service = snapshot.getValue(Service.class);
                                if (service != null) {
                                    String user = service.getServiceProviderUser();
                                    service.setDisabled(!isOffered);
                                    updateMap.put(PROVIDED_SERVICES + "/" + serviceType + user,
                                            service);
                                }
                            }

                            myRef.updateChildren(updateMap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /* End Service/Service Type Methods */

    /**
     * Gets the availabilities of the user should they exist, otherwise returns an empty optional.
     * @return an rxjava observable of an optional weekly availabilities class.
     */
    @Override
    public Observable<Optional<WeeklyAvailabilities>> getAvailabilities() {
        return availabilitiesRepository.getAvailabilities();
    }

    /**
     * gets the availabilities of the user specified should they exist, otherwise returns an empty optional
     * @param username the username to get the availabilities for
     * @return an rxjava observable of an optional weekly availabilities class for the user.
     */
    @Override
    public Observable<Optional<WeeklyAvailabilities>> getAvailabilities(String username) {
        return availabilitiesRepository.getAvailabilities(username);
    }

    /**
     * Gets all availabilities for all service providers hashmap form, for fast lookup. Wrapped into
     * an rxjava observable.
     * @return Hashmap containing username as key and availabilities as value in an RxJava Observable.
     */
    public Observable<HashMap<String, WeeklyAvailabilities>> getAllAvailabilities() {
        return availabilitiesRepository.getAllAvailabilities();
    }

    /**
     * Sets the availabilities in the database for the current user.
     * @param availabilities the availabilities to set for the current user.
     */
    @Override
    public void setAvailabilities(WeeklyAvailabilities availabilities) {
        availabilitiesRepository.setAvailabilities(availabilities);
    }

    /** End Availabilities Logic **/

    /** Begin Booking Logic **/
    public Observable<List<Booking>> getAllBookingsForServiceOnDate(ReadOnlyService service, String date) {
        String provider = service.getServiceProviderUser();
        String type = service.getType();
        return Observable.create(subscriber ->
                myRef.child(BOOKINGS).orderByChild("providerServiceTypeParsableYearMonthDay")
                        .equalTo(provider + type + String.valueOf(true) + date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        getAllGenericOnDataChange(dataSnapshot, subscriber, Booking.class);
                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));

    }

    //TODO ALLOW CHANGING AVAILABILITIES TO DELETE BOOKINGS.
    // Will be used for resetting availabilities
    public Observable<List<Booking>> getAllBookingsForServiceOnDay(ReadOnlyService service, DayOfWeek day) {
        String provider = service.getServiceProviderUser();
        String type = service.getType();
        return Observable.create(subscriber ->
                myRef.child(BOOKINGS).orderByChild("providerServiceTypeParsableDayOfWeek")
                        .equalTo(provider + type + String.valueOf(true) + day.toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                getAllGenericOnDataChange(dataSnapshot, subscriber, Booking.class);
                            }

                            @Override // Only really called when the database doesn't give enough permissions.
                            public void onCancelled(@NonNull DatabaseError dbError) {
                                if (subscriber.isDisposed())
                                    return;
                                subscriber.onError(new FirebaseException(dbError.getMessage()));
                            }
                        }));
    }



    public void saveBooking(Booking booking) {
        myRef.child(BOOKINGS).child(booking.getProviderServiceTypeParsableYearMonthDayCustomer())
                .setValue(booking);
    }

}
