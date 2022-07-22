const app = document.querySelector(".app");
const filesElement = document.querySelector(".files");
const uploadInput = document.querySelector(".upload .file-upload");
const uploadContainer = document.querySelector(".upload");
const uploadButton = document.querySelector(".upload-button");
const loginLogoutElement = document.querySelector(".loginLogout");
const changeModeElement = document.querySelector(".change-mode");
let currentMode = "view";
let files = [];

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
(async () => {
    const materialIcon = (name) => {
        const icon = document.createElement("span");
        icon.classList.add("material-icons");
        icon.innerHTML = name;
        return icon;
    };
    const getFiles = async () => {
        files = await fetch(`/files`, {
            credentials: "include",
        }).then((res) => {
            if (res.status === 401) {
                return [];
            }
            return res.json();
        });
    };
    const renderFiles = () => {
        while (filesElement.firstChild) {
            filesElement.removeChild(filesElement.firstChild);
        }
        files.forEach((file) => {
            const fileExtension = file.name.split(".").pop().toLowerCase();
            const fileIcon = fileIconMap[fileExtension] || "file";
            // add to DOM
            const fileElement = document.createElement("div");
            fileElement.classList.add("file");
            fileElement.onclick = async () => {
                if (currentMode === "view") {
                    window.open(file.url, "_blank");
                } else {
                    console.log(file);
                    await fetch(`/file/${file.id}`, {
                        credentials: "include",
                        method: "DELETE",
                    });
                    await getFiles();
                    renderFiles();
                }
            };
            {
                const imageElement = document.createElement("img");
                imageElement.src = `/icon/${fileIcon}.png`;
                fileElement.appendChild(imageElement);
            }
            {
                const fileNameElement = document.createElement("p");
                fileNameElement.innerHTML = file.name;
                fileElement.appendChild(fileNameElement);
            }
            {
                if (currentMode === "delete") {
                    const overlay = document.createElement("div");
                    overlay.classList.add("overlay");
                    const deleteBtn = materialIcon("delete");
                    deleteBtn.classList.add("delete-btn");
                    fileElement.appendChild(overlay);
                    fileElement.appendChild(deleteBtn);
                }
            }
            filesElement.appendChild(fileElement);
        });
    };
    const authRelateRender = async () => {
        const auth = await fetch(`/auth`, {
            credentials: "include",
        }).then((res) => {
            if (res.status === 401) {
                return false;
            } else {
                return true;
            }
        });
        // login and logout area
        while (loginLogoutElement.firstChild) {
            loginLogoutElement.removeChild(loginLogoutElement.firstChild);
        }
        switch (auth) {
            case true: {
                const logOutButton = document.createElement("button");
                logOutButton.innerHTML = "Logout";
                logOutButton.onclick = () => {
                    document.cookie = "token=";
                    reload();
                };
                loginLogoutElement.appendChild(logOutButton);
                break;
            }
            case false: {
                const label = document.createElement("label");
                label.innerHTML = "Password";
                label.htmlFor = "loginPassword";
                loginLogoutElement.appendChild(label);
                const input = document.createElement("input");
                input.type = "password";
                input.id = "loginPassword";
                input.placeholder = "Password";
                loginLogoutElement.appendChild(input);
                const loginButton = document.createElement("button");
                loginButton.innerHTML = "Login";
                loginButton.onclick = async () => {
                    document.cookie = "token=" + input.value;
                    loginButton.disabled = true;
                    await reload();
                    loginButton.disabled = false;
                };
                loginLogoutElement.appendChild(loginButton);
                break;
            }
        }
        // upload file area
        while (uploadContainer.firstChild) {
            uploadContainer.removeChild(uploadContainer.firstChild);
        }
        if (auth) {
            let uploadFileList = [];
            const uploadFile = document.createElement("input");
            uploadFile.type = "file";
            uploadFile.classList.add("file-upload");
            uploadFile.multiple = true;
            uploadFile.onchange = (event) => {
                uploadFileList = [...event.target.files];
                console.log(uploadFileList);
            };

            const uploadBtn = document.createElement("button");
            uploadBtn.innerHTML = "Upload";
            uploadBtn.classList.add("upload-button");
            uploadBtn.onclick = async () => {
                uploadBtn.disabled = true;
                if (uploadFileList.length === 0) {
                    return;
                }
                const formData = new FormData();
                uploadFileList.forEach((file) => {
                    formData.append("files", file);
                });
                await fetch(`/upload`, {
                    method: "POST",
                    credentials: "include",
                    body: formData,
                });
                await getFiles();
                renderFiles();
                uploadFileList = [];
                uploadFile.value = [];
                uploadBtn.disabled = false;
            };

            uploadContainer.appendChild(uploadFile);
            uploadContainer.appendChild(uploadBtn);
        }

        // change view or delete mode
        while (changeModeElement.firstChild) {
            changeModeElement.removeChild(changeModeElement.firstChild);
        }
        if (auth) {
            changeModeElement.style.display = "block";
            changeModeElement.style.visibility = "visible";
            changeModeElement.appendChild(materialIcon("file_download"));
            changeModeElement.onclick = () => {
                currentMode = currentMode === "delete" ? "view" : "delete";
                while (changeModeElement.firstChild) {
                    changeModeElement.removeChild(changeModeElement.firstChild);
                }
                if (currentMode === "view") {
                    changeModeElement.appendChild(
                        materialIcon("file_download"),
                    );
                } else {
                    changeModeElement.appendChild(materialIcon("delete"));
                }
                renderFiles();
            };
        } else {
            changeModeElement.style.display = "none";
            changeModeElement.style.visibility = "hidden";
        }
    };

    const reload = async () => {
        authRelateRender();
        await getFiles();
        renderFiles();
    };
    await reload();
})();
