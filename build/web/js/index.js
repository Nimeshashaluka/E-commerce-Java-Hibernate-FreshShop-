async function checkLogIn() {
    const response = await fetch(
            "CheckLogIn"
            );

    if (response.ok) {
        const json = await response.json();
//        const popup = Notification();

        if (json.success) {

            const user = json.content;
            console.log(user);

            let  logoutlink = document.getElementById("logOut-link");

            let logoutlink1 = document.getElementById("logOut-link1")
            logoutlink1.remove();
            let logoutlink2 = document.getElementById("logOut-link2")
            logoutlink2.remove();

            let newlinkTag = document.createElement("a");
            newlinkTag.innerHTML = user.first_name + " " + user.last_name;
            logoutlink.appendChild(newlinkTag);

            let logOutButton1 = document.getElementById("logOutButton");
            logOutButton1.href = "LogOut";
            logOutButton1.innerHTML = "Sign Out";

//            let FnameM1 = document.getElementById("FnameM");

            let Fname1 = document.getElementById("userFname");
            Fname1.innerHTML = user.first_name;

            let Lname1 = document.getElementById("userLname");
            Lname1.innerHTML = user.last_name;

            let Email1 = document.getElementById("email1");
            Email1.innerHTML = user.email;


        } else {
            console.log("Not Signed In");
        }


    }
}