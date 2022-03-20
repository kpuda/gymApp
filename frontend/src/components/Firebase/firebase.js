import app from 'firebase/app';
import 'firebase/auth';
import 'firebase/database';

const config = {
    apiKey: "AIzaSyB-Apof01nwj74LtHm6aP-O3uoP3KddRCA",
    authDomain: "my-app-3af38.firebaseapp.com",
    databaseURL: "https://my-app-3af38-default-rtdb.europe-west1.firebasedatabase.app",
    projectId: "my-app-3af38",
    storageBucket: "my-app-3af38.appspot.com",
    messagingSenderId: "300471049543",
    appId: "1:300471049543:web:a5becb4f3bb1f9e9b28062",
    measurementId: "G-SVCTG5VNH6"
};

class Firebase {
    constructor() {
        app.initializeApp(config);
        this.auth = app.auth();
        this.db = app.database();
    }

    /*** Authentication  ***/
    doCreateUserWithEmailAndPassword = (email, password) =>
        this.auth.createUserWithEmailAndPassword(email, password);

    doSignInWithEmailAndPassword = (email, password) =>
        this.auth.signInWithEmailAndPassword(email, password);

    doSignOut = () =>
        this.auth.signOut();

    doPasswordReset = email =>
        this.auth.sendPasswordResetEmail(email);

    /*** Database ***/
    user = uid => this.db.ref(`users/${uid}`);
    users = () => this.db.ref('users');

    addActivity = (uid, activity) => {
        const ref = this.db.ref().child(`users/${uid}/activities`);
        ref.push(activity);
    };

    updateActivity = (uid, activity, activityKey) => {
        const ref = this.db.ref().child(`users/${uid}/activities/${activityKey}`);
        ref.update(activity);
    }
}

export default Firebase;
