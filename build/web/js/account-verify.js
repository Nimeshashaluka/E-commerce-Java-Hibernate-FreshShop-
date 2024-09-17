async function verifyAccount() {
    console.log("verify");

    const dto = {
        verifyCode: document.getElementById("verifyCode").value,
    };

    console.log(dto);

    const response = await fetch(
            "Verification",
            {
                method: "POST",
                body: JSON.stringify(dto),
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