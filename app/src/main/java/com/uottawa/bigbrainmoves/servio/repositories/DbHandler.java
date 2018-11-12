package com.uottawa.bigbrainmoves.servio.repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.SignupResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DbHandler implements Repository {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
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

                            if (emailId.equals("")) {
                                subscriber.onNext(false);
                                subscriber.onComplete();
                            } else {
                                // handles the successful email case.

                                /* first make the observer which will pass on data from the
                                   helper loginToAccount method to the current observable.
                                   Required due to the nested callback.
                                 */
                                Observer<Boolean> booleanObserver = new Observer<Boolean>() {
                                    Disposable disposable;

                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        disposable = d;
                                    }

                                    // gets boolean from the login helper.
                                    @Override
                                    public void onNext(Boolean bool) {
                                        subscriber.onNext(bool);
                                        subscriber.onComplete();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        subscriber.onError(e);
                                        disposable.dispose();
                                        disposable = null;
                                    }

                                    // do some cleanup
                                    @Override
                                    public void onComplete() {
                                        disposable.dispose();
                                        disposable = null;
                                    }
                                };
                                // subscribe to the loginToAccount event to return the right thing.
                                loginToAccount(emailId, password).subscribe(booleanObserver);
                            }
                        }

                        @Override // database permission error
                        public void onCancelled(@NonNull DatabaseError dbError) {
                            if (subscriber.isDisposed())
                                return;
                            subscriber.onError(new FirebaseException(dbError.getMessage()));
                        }
                    });
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

                                Account account = dataSnapshot.getValue(Account.class);
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
                            Account account = new Account(displayName, type, username);
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
        if (account.type.equals("admin")) {
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
            // We make sure that a user does not exist with this uid.
            myRef.child("user_ids").child(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (subscriber.isDisposed())
                                return;
                            // user doesn't exist
                            if (!dataSnapshot.exists()) {
                                // user does not exist
                                Observer<SignupResult> signUpObserver = new Observer<SignupResult>() {
                                    Disposable disposable;

                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        disposable = d;
                                    }

                                    // gets boolean from the login helper.
                                    @Override
                                    public void onNext(SignupResult result) {
                                        subscriber.onNext(result);
                                        subscriber.onComplete();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        subscriber.onError(e);
                                        disposable.dispose();
                                        disposable = null;
                                    }

                                    // do some cleanup
                                    @Override
                                    public void onComplete() {
                                        disposable.dispose();
                                        disposable = null;
                                    }
                                };
                                createAccount(email,
                                              username,
                                              password,
                                              displayName,
                                              typeSelected).subscribe(signUpObserver);

                            } else {
                                /*
                                Toast.makeText(getApplicationContext(), "Account taken", Toast.LENGTH_LONG).show();
                                */
                                subscriber.onNext(SignupResult.USERNAME_TAKEN);
                                subscriber.onComplete();
                            }
                        }

                        @Override // Some kind of error
                        public void onCancelled(@NonNull DatabaseError dbError) {
                            if (subscriber.isDisposed())
                                return;
                            subscriber.onError(new FirebaseException(dbError.getMessage()));
                        }
                    });
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

    public void deleteServiceType(String serviceTypeName) {
        myRef.child("service_types").child(serviceTypeName).removeValue();
    }

    public void editServiceType(String serviceTypeName, double value) {
        writeServiceTypeToDatabase(serviceTypeName, value);
    }

}


