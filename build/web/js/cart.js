async function loadCartItems() {
    const response = await fetch(
            "LoadCartItem"
            );

    if (response.ok) {
        const json = await response.json();
        const popup = Notification();

        if (json.length == 0) {
            popup.warning({

                message: "Your Cart is Empty"
            });
        } else {
            let cartItemContainer = document.getElementById("cartItemContainer");
            let cartItemRow = document.getElementById("cartItemRow");

            cartItemContainer.innerHTML = "";

            let totalQty = 0;
            let total = 0;
            let gTotal =250;
            json.forEach(item => {

                let itemSubTotal = item.product.price * item.qty;
                totalQty += item.qty;
                total += itemSubTotal;
                gTotal += item.product.price * item.qty;

                let cartItemRowClone = cartItemRow.cloneNode(true);
                cartItemRowClone.querySelector("#cartItem-a").href = "singleProductView.html?id=" + item.product.id;
                cartItemRowClone.querySelector("#cartItemImage").src = "productImage/" + item.product.id + "/image1.png";
                cartItemRowClone.querySelector("#cartItemTitle").innerHTML = item.product.title;
                cartItemRowClone.querySelector("#cartItemPrice").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(item.product.price);
                cartItemRowClone.querySelector("#cartItemQty").value = item.qty;
                cartItemRowClone.querySelector("#cartItemSubTotal").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(itemSubTotal);

                cartItemContainer.appendChild(cartItemRowClone);

            });
            document.getElementById("cartSubTotal").innerHTML = "Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(total);
            document.getElementById("grandTotal").innerHTML = "Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(gTotal);

            
        }


    } else {
        popup.error({

            message: "Unable To Process Your Request."
        });
    }

}