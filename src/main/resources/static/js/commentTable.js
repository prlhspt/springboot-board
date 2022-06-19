const headers = document.getElementsByClassName("header");
const contents = document.getElementsByClassName("content");

const headers2 = document.getElementsByClassName("header2");
const contents2 = document.getElementsByClassName("content2");

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

for (let i = 0; i < headers2.length; i++) {
    headers2[i].addEventListener("click", () => {
        for (let j = 0; j < contents2.length; j++) {
            if (i == j) {
                contents2[j].style.display = contents2[j].style.display == "block" ? "none" : "block";
            } else {
                contents2[j].style.display = "none";
            }
        }
    });
}