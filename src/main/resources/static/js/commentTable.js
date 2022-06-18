const headers = document.getElementsByClassName("header");
const contents = document.getElementsByClassName("content");

for (let i = 0; i < headers.length; i++) {
    headers[i].addEventListener("click", () => {
        for (let j = 0; j < contents.length; j++) {
            if (i == j) {
                contents[j].style.display = contents[j].style.display == "block" ? "none" : "block";
            } else {
                contents[j].style.display = "none";
            }
        }
    });
}