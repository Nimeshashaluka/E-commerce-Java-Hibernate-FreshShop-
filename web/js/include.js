function headerload() {
    // Load header.html content into the #header-container div
    const xhr = new XMLHttpRequest();
    xhr.open('GET', 'header.html', true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById('header-container').innerHTML = xhr.responseText;
        }
    };
    xhr.send();
}