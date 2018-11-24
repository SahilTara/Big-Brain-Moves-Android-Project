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
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.SignupResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;

public class DbHandler implements Repository {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AvailabilitiesRepository availabilitiesRepository = new AvailabilitiesRepositoryFirebase();

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
        return Observable.create(subscriber -> {
            myRef.child("user_ids").child(username)
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
                    });
        }).flatMap(result -> {
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
        return Observable.create(subscriber -> {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success

                            subscriber.onNext(true);
                            subscriber.onComplete();
                        } else { // otherwise sign in failed.
                            subscriber.onNext(false);
                            subscriber.onComplete();
                        }
                    });
        });
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
        return Observable.create(subscriber -> {
            myRef.child("user_info").addListenerForSingleValueEvent(new ValueEventListener() {
                //
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (subscriber.isDisposed())
                        return;
                    // We have some users in the database.
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
                }

                @Override // Only really called when the database doesn't give enough permissions.
                public void onCancelled(@NonNull DatabaseError dbError) {
                    if (subscriber.isDisposed())
                        return;
                    subscriber.onError(new FirebaseException(dbError.getMessage()));
                }
            });
        });

    }


    /**
     * Gets an Account Object from a FireBase base id, and returns an observable using that id.
     * @param uid the uid of the FireBase user
     * @return an rxJava observable of the account.
     */
    public Observable<Optional<Account>> getUserFromDataBase(String uid) {
        return Observable.create(subscriber -> {
            myRef.child("user_info").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        //
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // if the subscriber is disposed we don't care about result
                                if (subscriber.isDisposed())
                                    return;

                                //TODO: Try to clean this up later
                                Account account = dataSnapshot.getValue(Account.class);

                                if (account.getType().toLowerCase().equals("service")) {
                                    account = dataSnapshot.getValue(ServiceProvider.class);
                                }

                                Optional<Account> accountOptional = Optional.ofNullable(account);

                                subscriber.onNext(accountOptional);
                                subscriber.onComplete();
                            } else {
                                subscriber.onNext(Optional.empty());
                            }
                        }

                        @Override // Only really called when the database doesn't give enough permissions.
                        public void onCancelled(@NonNull DatabaseError dbError) {
                            // if the subscriber is disposed we don't care about result
                            if (subscriber.isDisposed())
                                return;

                            subscriber.onError(new FirebaseException(dbError.getMessage()));
                        }
                    });
        });


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
                                                   final String type) {
        return Observable.create(subscriber -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in account's information
                            Account account;

                            // TODO: REPLACE WITH ENUM LATER
                            if (type.toLowerCase().equals("service")) {
                                // Service Provider class polymorphically created.
                                account = new ServiceProvider(displayName, type, username,
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
                    });
        });
    }

    /**
     * Adds the displayName, type to the database under the uid.
     *
     */
    private void writeUserToDataBase(String uid, Account account) {
        myRef.child("user_info").child(uid).setValue(account);
        if (account.getType().equals("admin")) {
            // If we write admin type to database we have to do some things to make sure no more admins are made.
            myRef.child("admin_exists").setValue(true);
        }
    }

    /**
     * Writes the username : email to database for username login.
     * @param username username specified by the user.
     * @param email email specified by the user.
     */
    private void writeUserNameToDatabase(String username, String email) {
        myRef.child("user_ids").child(username).setValue(email);
    }

    /**
     * Method to check if an admin account exists.
     * @return the existence of an admin account.
     */
    public Observable<Boolean> doesAdminAccountExist() {

        return Observable.create(subscriber -> {
            myRef.child("admin_exists").addListenerForSingleValueEvent(new ValueEventListener() {

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
            });
        });
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
                                                              final String typeSelected) {
        return Observable.create(subscriber -> {
            // We make sure that a user does not exist with this username.
            // We will return true if the user exists, false otherwise in this observer.
            myRef.child("user_ids").child(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (subscriber.isDisposed())
                                return;
                            // user doesn't exist
                            if (!dataSnapshot.exists()) {

                                subscriber.onNext(false);
                            } else {
                                // Username is taken
                                subscriber.onNext(true);
                            }
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
        if(checkIfUserIsLoggedIn()) {
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
            myRef.child("service_types").child(serviceTypeName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (subscriber.isDisposed())
                                return;
                            if (!dataSnapshot.exists()) {
                                writeServiceTypeToDatabase(serviceTypeName, value);
                                startOfferingAllServicesWithType(serviceTypeName);
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
        myRef.child("service_types").child(serviceTypeName).setValue(serviceType);
    }



    /**
     * Method listens for changes in the service types database, and notifies all observers.
     * @return RxJava containing a service type and whether removed or not.
     */
    public Observable<Pair<ServiceType, Boolean>> listenForServiceTypeChanges() {
        return Observable.create(subscriber -> {
           myRef.child("service_types").addChildEventListener(new ChildEventListener() {
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
           });
        });
    }

    /**
     * Deletes the specified service type from the database.
     * @param serviceTypeName name of service type to delete
     */
    public void deleteServiceType(String serviceTypeName) {
        myRef.child("service_types").child(serviceTypeName).removeValue();
        stopOfferingAllServicesWithType(serviceTypeName);
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
     * Method simply returns all service type names within an rxjava observable.
     * @return rxjava observable containing all current service type names.
     */
    private Observable<List<String>> getAllServiceTypeNames() {
        return Observable.create(subscriber -> {
            myRef.child("service_types").addListenerForSingleValueEvent(new ValueEventListener() {
                //
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (subscriber.isDisposed())
                        return;
                    // We have some services in the database for the user
                    if (dataSnapshot.exists()) {
                        ArrayList<String> serviceTypes = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ServiceType serviceType = snapshot.getValue(ServiceType.class);
                            if (serviceType != null) {
                                serviceTypes.add(serviceType.getType());
                            }
                        }
                        subscriber.onNext(serviceTypes);
                        subscriber.onComplete();
                    } else {
                        // deal with no services case
                        subscriber.onNext(Collections.emptyList());
                        subscriber.onComplete();
                    }
                }

                @Override // Only really called when the database doesn't give enough permissions.
                public void onCancelled(@NonNull DatabaseError dbError) {
                    if (subscriber.isDisposed())
                        return;
                    subscriber.onError(new FirebaseException(dbError.getMessage()));
                }
            });
        });
    }

    /**
     * Gets all active services provided by the current user in the database.
     * @return rxjava observable containing all active services of user.
     */
    @Override
    public Observable<List<Service>> getServicesProvidedByCurrentUser() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String username = account.getUsername();

        return Observable.create(subscriber -> {
            myRef.child("currently_provided").orderByChild("usernameOfferedDisabled")
                    .equalTo(username + String.valueOf(true) + false).addListenerForSingleValueEvent(new ValueEventListener() {
                //
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (subscriber.isDisposed())
                        return;

                    // We have some services in the database for the user
                    if (dataSnapshot.exists()) {
                        ArrayList<Service> services = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Service service = snapshot.getValue(Service.class);
                            if (service != null) {
                                services.add(service);
                            }
                        }
                        subscriber.onNext(services);
                        subscriber.onComplete();
                    } else {
                        // deal with no services case
                        subscriber.onNext(Collections.emptyList());
                        subscriber.onComplete();
                    }
                }

                @Override // Only really called when the database doesn't give enough permissions.
                public void onCancelled(@NonNull DatabaseError dbError) {
                    if (subscriber.isDisposed())
                        return;
                    subscriber.onError(new FirebaseException(dbError.getMessage()));
                }
            });
        });
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
        return getAllServiceTypeNames().flatMapIterable(serviceTypes -> serviceTypes) // turns into an iterable stream
                .filter(item->!provided.contains(item)) // filters based on contains
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

        return Observable.create(subscriber -> {
            myRef.child("currently_provided").orderByChild("usernameOfferedDisabled")
                    .equalTo(username + String.valueOf(false) + false).addListenerForSingleValueEvent(new ValueEventListener() {
                //
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                            services.add(new Service(serviceType, 0.0, username, false, false));
                        }


                        subscriber.onNext(services);
                        subscriber.onComplete();
                    } else {
                        // deal with no services case
                        ArrayList<Service> services = new ArrayList<>();
                        for (String serviceType : serviceTypes) {
                            services.add(new Service(serviceType, 0.0, username, false, false));
                        }
                        subscriber.onNext(services);
                        subscriber.onComplete();
                    }

                }

                @Override // Only really called when the database doesn't give enough permissions.
                public void onCancelled(@NonNull DatabaseError dbError) {
                    if (subscriber.isDisposed())
                        return;
                    subscriber.onError(new FirebaseException(dbError.getMessage()));
                }
            });
        });
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
        String type = account.getType();
        String username = account.getUsername();

        HashMap<String, Object> updateMap = new HashMap<>();

        // specify the services to be updated
        for (Service service : modified) {
            updateMap.put("currently_provided/" + service.getType() + username, service);
        }

        // specify the user to be updated
        updateMap.put("user_info/" + getUserId(),
                new ServiceProvider(displayName, type, username, phoneNumber, address, companyName,
                        description, isLicensed));

        return Observable.create(subscriber -> {
            myRef.updateChildren(updateMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // successful update
                    subscriber.onNext(true);
                    subscriber.onComplete();
                } else { // otherwise unsuccessful
                    subscriber.onNext(false);
                    subscriber.onComplete();
                }
            });
        });
    }

    /**
     * Stops offering services with similar types
     */
    private void stopOfferingAllServicesWithType(String serviceType) {
        myRef.child("currently_provided").orderByChild("typeDisabled").equalTo(serviceType + false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> updateMap = new HashMap<>();
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Service service = snapshot.getValue(Service.class);
                                if (service != null) {
                                    String user = service.getServiceProviderUser();
                                    service.setDisabled(true);
                                    updateMap.put("currently_provided/" + serviceType + user,
                                           service);
                                }
                            }

                            // TODO add on complete listener??
                            myRef.updateChildren(updateMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Start offering services with specified type name if such exist.
     */

    private void startOfferingAllServicesWithType(String serviceType) {
        myRef.child("currently_provided").orderByChild("typeDisabled").equalTo(serviceType + true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> updateMap = new HashMap<>();
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Service service = snapshot.getValue(Service.class);
                                if (service != null) {
                                    String user = service.getServiceProviderUser();
                                    service.setDisabled(false);
                                    updateMap.put("currently_provided/" + serviceType + user,
                                            service);
                                }
                            }

                            // TODO add on complete listener??
                            myRef.updateChildren(updateMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public void setAvailabilities(WeeklyAvailabilities availabilities) {
        availabilitiesRepository.setAvailabilities(availabilities);
    }

}
