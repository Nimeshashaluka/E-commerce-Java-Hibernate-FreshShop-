async function loadData() {
//    console.log("Done");
    const response = await fetch("LoadDatas");

    const popup = Notification();

    if (response.ok) {
        const json = await response.json();
        console.log(json);


        loadOption("category", json.categoryList, "name");
        loadOption("model", json.modelList, "name");


        updateProductView(json);

    } else {
        popup.error({
            message: "Try again later"
        });
    }

}

function loadOption(prefix, dataList, property) {
    let options = document.getElementById(prefix + "-options");
    let li = document.getElementById(prefix + "-list");
    options.innerHTML = "";

    dataList.forEach(data => {
        let li_clone = li.cloneNode(true);
        li_clone.querySelector("#" + prefix + "-name").innerHTML = data[property];
        options.appendChild(li_clone);
    });

    //from template js
    const all_li = document.querySelectorAll("#" + prefix + "-options li");
    all_li.forEach(x => {
        x.addEventListener('click', function () {
            all_li.forEach(y => y.classList.remove('chosen'));
            this.classList.add('chosen');
        });
    });
}

async function searchProducts(firstResult) {

    const popup = Notification();


    //get search data
    let category_name = document.getElementById("category-options")
            .querySelector(".chosen")?.querySelector("a").innerHTML;

//    console.log(category_name);

    let model_name = document.getElementById("model-options")
            .querySelector(".chosen")?.querySelector("a").innerHTML;
//    console.log(model_name);

    let sort_text = document.getElementById("st-sort").value;
    console.log(sort_text);
    const data = {
        firstResult: firstResult,
        category_name: category_name,
        model_name: model_name,
        sort_text: sort_text,
    };

    const response = await fetch("SearchProducts", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();
        console.log(json);
//        updateProductView(json);
        //currentPage = 0;
        if (json.success) {

            updateProductView(json);

            popup.success({
                message: "Search Completed!"
            });
        } else {
            popup.error({
                message: "Try again later!"
            });
        }
    } else {
        popup.error({
            message: "Try again later"
        });
    }
}

let st_product = document.getElementById("st-product");
let st_pagination_button = document.getElementById("st-pagination-button");

var currentPage = 0;

function updateProductView(json) {

    let st_product_container = document.getElementById("st-product-container");
    st_product_container.innerHTML = "";

    json.productList.forEach(product => {
//            console.log(product.id);
        let st_product_clone = st_product.cloneNode(true);

        st_product_clone.querySelector("#st-product-a-1").href = "singleProductView.html?id=" + product.id;
        st_product_clone.querySelector("#st-product-img-1").src = "productImage/" + product.id + "/image1.png";
        st_product_clone.querySelector("#st-product-title-1").innerHTML = product.title;
        st_product_clone.querySelector("#st-product-price-1").innerHTML = new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                }
        ).format(product.price);


        st_product_container.appendChild(st_product_clone);
    });

    let st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";

    let product_count = json.allProductCount;
    const product_per_page = 8;

    let pages = Math.ceil(product_count / product_per_page);

//    let st_pagination_button_clone_prev = st_pagination_button.cloneNode(true);
//    st_pagination_button_clone_prev.innerHTML = "Prev";
//    st_pagination_container.appendChild(st_pagination_button_clone_prev);
//
//    



    //add previous button
    if (currentPage != 0) {
        let st_pagination_button_clone_prev = st_pagination_button.cloneNode(true);
        st_pagination_button_clone_prev.innerHTML = "Prev";

        st_pagination_button_clone_prev.addEventListener("click", e => {
            currentPage--;
            searchProducts(currentPage * 8);
        });

        st_pagination_container.appendChild(st_pagination_button_clone_prev);
    }

    //add page buttons
    for (let i = 0; i < pages; i++) {
        let st_pagination_button_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_clone.innerHTML = i + 1;

        st_pagination_button_clone.addEventListener("click", e => {
            currentPage = i;
            searchProducts(i * 8);
        });

        if (i === currentPage) {
            st_pagination_button_clone.className = "axil-btn  btn-bg-lighter  text-dark btn-outlin-dark me-2";
//            st_pagination_button_clone.className = "axil-btn btn-bg-secondary me-2";

        } else {
            st_pagination_button_clone.className = "axil-btn btn-bg-primary text-light me-2";
        }

        st_pagination_container.appendChild(st_pagination_button_clone);
    }

    //add Next button
    if (currentPage != (pages - 1)) {
        let st_pagination_button_clone_next = st_pagination_button.cloneNode(true);
        st_pagination_button_clone_next.innerHTML = "Next";

        st_pagination_button_clone_next.addEventListener("click", e => {
            currentPage++;
            searchProducts(currentPage * 8);
        });

        st_pagination_container.appendChild(st_pagination_button_clone_next);
    }



}

async function searchProductByName() {
    const popup = Notification();

    // Get the search query
    const searchQuery = document.getElementById("productSearchInput").value.trim();
    if (!searchQuery) {
        popup.error({
            message: "Please enter a product name to search."
        });
        return;
    }

    // Prepare the data for the request
    const data = {
        searchQuery: searchQuery,
        category_name: document.querySelector("#category-options .chosen a")?.innerHTML || null,
        model_name: document.querySelector("#model-options .chosen a")?.innerHTML || null,
    };

    try {
        // Send the request to the SearchProduct servlet
        const response = await fetch("SearchProduct", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            const json = await response.json();
            console.log(json);

            if (json.success) {
                updateProductView(json);
                popup.success({
                    message: "Products matching your search were found."
                });
            } else {
                popup.error({
                    message: "No products matched your search."
                });
            }
        } else {
            popup.error({
                message: "Failed to search products. Please try again later."
            });
        }
    } catch (error) {
        console.error(error);
        popup.error({
            message: "An error occurred. Please try again later."
        });
    }
}


async function searchField(firstResult) {
//    alert("okk");

    let searchProduct = document.getElementById("searchInput").value;
    console.log(searchProduct);

    const data = {
        firstResult: firstResult,
        search_text: searchProduct,
        sort_text: "Short by Latest",
    };

    const response = await fetch("SearchProducts", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();
        console.log(json);
//        updateProductView(json);
        //currentPage = 0;
        if (json.success) {

            updateProductView(json);

            popup.success({
                message: "Search Completed!"
            });
        } else {
            popup.error({
                message: "Try again later!"
            });
        }
    } else {
        popup.error({
            message: "Try again later"
        });
    }
}

function ReloadPage() {
    alert("done");
    window.location.reload(true);
}