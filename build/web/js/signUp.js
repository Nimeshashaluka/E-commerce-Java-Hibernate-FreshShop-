async function signUp() {

    const user_dto = {
        first_name: document.getElementById("firstname").value,
        last_name: document.getElementById("lastname").value,
        mobile: document.getElementById("mobile").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
    };

    const response = await fetch(
            "SignUp",
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
                       window.location = "verify.html";
        } else {
            popup.warning({

                message: json.content
            });


        }
    } else {
        popup.error({

            message: "Please try again later!"
        });
    }
}

