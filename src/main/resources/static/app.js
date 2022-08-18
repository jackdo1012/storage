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

const app = document.querySelector(".app");
const filesElement = document.querySelector(".files");
const uploadContainer = document.querySelector(".upload");
const loginLogoutElement = document.querySelector(".loginLogout");
const changeModeElement = document.querySelector(".change-mode");
const backButton = document.querySelector(".back-btn");
let currentMode = "view";
let files = [];
let folders = [];

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
    const folderPath = new Stack();
    folderPath.push(
        await fetch(`/api/folder/root`, {
            credentials: "include",
        }).then((res) => res.json()),
    );
    const materialIcon = (name) => {
        const icon = document.createElement("span");
        icon.classList.add("material-icons");
        icon.innerHTML = name;
        return icon;
    };

    backButton.onclick = async () => {
        folderPath.pop();
        await getFilesAndFolders();
        renderFilesAndFolders();
    };

    const getFilesAndFolders = async () => {
        files = await fetch(`/api/file/${folderPath.peek().id}`, {
            credentials: "include",
        }).then((res) => {
            if (res.status === 401) {
                return [];
            }
            return res.json();
        });
        folders = await fetch(`/api/folder/${folderPath.peek().id}`, {
            credentials: "include",
        }).then((res) => {
            if (res.status === 401) {
                return [];
            }
            return res.json();
        });
    };
    const renderFilesAndFolders = () => {
        backButton.disabled = folderPath.length() <= 1;
        while (filesElement.firstChild) {
            filesElement.removeChild(filesElement.firstChild);
        }
        folders.forEach((folder) => {
            // add to DOM
            const folderElement = document.createElement("div");
            folderElement.classList.add("file");
            folderElement.onclick = async () => {
                if (currentMode === "view") {
                    folderPath.push(folder);
                    await getFilesAndFolders();
                    renderFilesAndFolders();
                } else {
                    await fetch(`/api/folder/${folder.id}`, {
                        credentials: "include",
                        method: "DELETE",
                    });
                    await getFilesAndFolders();
                    renderFilesAndFolders();
                }
            };
            {
                const imageElement = document.createElement("img");
                imageElement.src = `/icon/folder.png`;
                folderElement.appendChild(imageElement);
            }
            {
                const fileNameElement = document.createElement("p");
                fileNameElement.innerHTML = folder.name;
                folderElement.appendChild(fileNameElement);
            }
            {
                if (currentMode === "delete") {
                    const overlay = document.createElement("div");
                    overlay.classList.add("overlay");
                    const deleteBtn = materialIcon("delete");
                    deleteBtn.classList.add("delete-btn");
                    folderElement.appendChild(overlay);
                    folderElement.appendChild(deleteBtn);
                }
            }
            filesElement.appendChild(folderElement);
        });
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
                    await fetch(`/api/file/${file.id}`, {
                        credentials: "include",
                        method: "DELETE",
                    });
                    await getFilesAndFolders();
                    renderFilesAndFolders();
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
        const auth = await fetch(`/api/auth`, {
            credentials: "include",
        }).then((res) => {
            return res.status !== 401;
        });
        // login and logout area
        while (loginLogoutElement.firstChild) {
            loginLogoutElement.removeChild(loginLogoutElement.firstChild);
        }
        switch (auth) {
            case true: {
                const logOutButton = document.createElement("button");
                logOutButton.classList.add("logout-btn");
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
                loginButton.classList.add("login-btn");
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
            const uploadFileDiv = document.createElement("div");
            const uploadFile = document.createElement("input");
            uploadFile.type = "file";
            uploadFile.classList.add("file-upload");
            uploadFile.multiple = true;
            uploadFile.onchange = (event) => {
                uploadFileList = [...event.target.files];
            };

            const uploadBtn = document.createElement("button");
            uploadBtn.innerHTML = "Upload";
            uploadBtn.classList.add("upload-button");
            uploadBtn.onclick = async () => {
                uploadBtn.disabled = true;
                if (uploadFileList.length === 0) {
                    uploadBtn.disabled = false;
                    return;
                }
                const formData = new FormData();
                uploadFileList.forEach((file) => {
                    formData.append("files", file);
                });
                await fetch(`/api/file/${folderPath.peek().id}`, {
                    method: "POST",
                    credentials: "include",
                    body: formData,
                });
                await getFilesAndFolders();
                renderFilesAndFolders();
                uploadFileList = [];
                uploadFile.value = [];
                uploadBtn.disabled = false;
            };
            uploadFileDiv.appendChild(uploadFile);
            uploadFileDiv.appendChild(uploadBtn);

            const newFolderDiv = document.createElement("div");
            const newFolderInput = document.createElement("input");
            newFolderInput.type = "text";
            newFolderInput.classList.add("new-folder-input");
            newFolderInput.placeholder = "New Folder";

            const newFolderBtn = document.createElement("button");
            newFolderBtn.classList.add("new-folder-btn");
            newFolderBtn.innerHTML = "New Folder";
            newFolderBtn.onclick = async () => {
                newFolderBtn.disabled = true;
                await fetch(`/api/folder/${folderPath.peek().id}`, {
                    method: "POST",
                    credentials: "include",
                    body: newFolderInput.value,
                });
                await getFilesAndFolders();
                renderFilesAndFolders();
                newFolderInput.value = "";
                newFolderBtn.disabled = false;
            };

            newFolderDiv.appendChild(newFolderInput);
            newFolderDiv.appendChild(newFolderBtn);

            uploadContainer.appendChild(newFolderDiv);
            uploadContainer.appendChild(uploadFileDiv);
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
                renderFilesAndFolders();
            };
        } else {
            changeModeElement.style.display = "none";
            changeModeElement.style.visibility = "hidden";
        }
    };

    const reload = async () => {
        authRelateRender();
        await getFilesAndFolders();
        renderFilesAndFolders();
    };
    await reload();
})();
