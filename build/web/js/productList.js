async function loadProduct() {
//    console.log("ok product");

    const response = await fetch(
            "LoadProduct"
            );

    if (response.ok) {
        const json = await response.json();
        const popup = Notification();

        console.log(json);

        let loadProductList = document.getElementById("mainProductImagList");
        let productImageView = document.getElementById("ProductImage-view");
        
        loadProductList.innerHTML="";

        json.forEach(item => {
                    console.log(item);

            let productClone = productImageView.cloneNode(true);
            productClone.querySelector("#ProductImage-view-a").href = "singleProductView.html?id=" + item.product.id;        
//            productClone.querySelector("#ProductImage-view-b").href = "singleProductView.html?id=" + item.product.id;

            productClone.querySelector("#productImage").src = "productImage/" + item.product.id + "/image1.png";
            productClone.querySelector("#productItemTi").innerHTML =item.product.title;        
            productClone.querySelector("#productPrice").innerHTML = item.product.price;

            loadProductList.appendChild(productClone);
//            console.log(item);
        });

    } else {

    }
}