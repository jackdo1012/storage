const app = document.querySelector(".app");
const filesElement = document.querySelector(".files");
const uploadContainer = document.querySelector(".upload");
const loginLogoutElement = document.querySelector(".loginLogout");
const backButton = document.querySelector(".back-btn");
const rightClickContainer = document.createElement("div");
const rightClickUl = document.createElement("ul");
const renameBtn = document.createElement("li");
const deleteBtn = document.createElement("li");

class Stack {
    items = [];

    push(item) {
        this.items.push(item);
    }

    pop() {
        return this.items.pop();
    }

    peek() {
        return this.items[this.items.length - 1];
    }

    length() {
        return this.items.length;
    }
}

const fileIconMap = {
    "7z": "7zip",
    apk: "apk",
    app: "app",
    cpp: "cpp",
    css: "css",
    dmg: "dmg",
    doc: "doc",
    docx: "docx",
    exe: "exe",
    gif: "gif",
    iso: "iso",
    jar: "java-archive",
    jpg: "jpg",
    jpeg: "jpg",
    js: "js",
    log: "log",
    m4a: "m4a",
    mov: "mov",
    mp3: "mp3",
    mp4: "mp4",
    pdf: "pdf",
    png: "png",
    pptx: "pptx",
    psd: "psd",
    py: "py",
    rar: "rar",
    craw: "raw",
    sql: "sql",
    txt: "txt",
    wav: "wav",
    wps: "wps",
    xls: "xls",
    xlsx: "xlsx",
    xml: "xml",
    zip: "zip",
};

export {
    app,
    filesElement,
    uploadContainer,
    loginLogoutElement,
    backButton,
    rightClickContainer,
    rightClickUl,
    renameBtn,
    deleteBtn,
    fileIconMap,
    Stack,
};
