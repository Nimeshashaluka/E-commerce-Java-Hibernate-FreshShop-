async  function signIn() {

    const user_dto = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,

    };
//    console.log(user_dto);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();
        const popup = Notification();

        if (json.success) {
            window.location = "index.html";

        } else {

            if (json.content == "Please Click the Verify Button and Verify the Your Account!") {
                popup.warning({

                    message: json.content
                });
            } else {
                popup.warning({

                    message: json.content
                });
            }


        }


    } else {
        popup.error({

            message: "Please try again later!"
        });
    }

}