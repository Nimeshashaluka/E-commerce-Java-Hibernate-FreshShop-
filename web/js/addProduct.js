var modelList;

async function loadData() {

    const response = await fetch(
            "LoadDatas",
            );

    if (response.ok) {
        const json = await response.json();
        const popup = Notification();

//        console.log(json);

        const categoryList = json.categoryList;
        modelList = json.modelList;
        const productSize = json.productSize;

        loadSelect("categorySelect", categoryList, "name");
        loadSelect("modelSelect", modelList, "name");
        loadSelect("productSize", productSize, "name");

    } else {
        popup.error({

            message: "Please try again later!"
        });
    }
}

function loadSelect(selectTagId, list, property) {
    const selectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item.id;
        optionTag.innerHTML = item[property];
        selectTag.appendChild(optionTag);

    });
}

function updateModels() {

    let modelSelectTag = document.getElementById("modelSelect");
    let selectedcategoryId = document.getElementById("categorySelect").value;
    modelSelectTag.length = 1;


    modelList.forEach(model => {
        if (model.category.id == selectedcategoryId) {
            let optionTag = document.createElement("option");
            optionTag.value = model.id;
            optionTag.innerHTML = model.name;
            modelSelectTag.appendChild(optionTag);
        }
    });

}

async  function productAdd() {
    const titleSelectTag = document.getElementById("title");
    const descriptionSelectTag = document.getElementById("description");
    const categorySelectTag = document.getElementById("categorySelect");
    const modelSelectTag = document.getElementById("modelSelect");
    const productSizeTag = document.getElementById("productSize");
    const priceSelectTag = document.getElementById("price");
    const quantitySelectTag = document.getElementById("quantity");

    const imageSelectTag = document.getElementById("image1");

    const data = new FormData();

    data.append("titleId", titleSelectTag.value);
    data.append("descriptionId", descriptionSelectTag.value);
    data.append("categoryId", categorySelectTag.value);
    data.append("modelId", modelSelectTag.value);
    data.append("productSizeId", productSizeTag.value);
    data.append("priceSelectId", priceSelectTag.value);
    data.append("quantitySelectId", quantitySelectTag.value);
    data.append("imageId", imageSelectTag.files[0]);

    const response = await fetch(
            "AddProduct",
            {
                method: "POST",
                body: data

            }
    );

    if (response.ok) {
        const json = await response.json();
        const popup = Notification();

        if (json.success) {
            
            categorySelectTag.value=0;
            modelSelectTag.length=1;
            titleSelectTag.value="";
            descriptionSelectTag.value="";
            productSizeTag.value=0;
            priceSelectTag.value="";
            quantitySelectTag.value=1;
            imageSelectTag.value=null;

            popup.success({

                message: json.content
            });
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