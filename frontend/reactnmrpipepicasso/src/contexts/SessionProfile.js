var SessionProfile = (function() {
    //https://medium.com/@atif.ali.ati/user-session-in-react-js-aa749bc4faf6
    var session = Math.random().toString(36).substr(2, 10);

    var getSession = function() {
        return session;    // Or pull this from cookie/localStorage
    };

    var setSession = function(name) {
        session = name;
        console.log('sono in SessionPrfile', name);
        // Also set this in cookie/localStorage
    };

    return {
        getSession: getSession,
        setSession: setSession
    }

})();

export default SessionProfile;