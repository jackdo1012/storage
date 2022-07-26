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
const rightClickContainer = document.createElement("div");
const rightClickUl = document.createElement("ul");
const renameBtn = document.createElement("li");
const deleteBtn = document.createElement("li");
let files = [];
let folders = [];

renameBtn.innerHTML = "Rename";
deleteBtn.innerHTML = "Delete";
rightClickUl.appendChild(renameBtn);
rightClickUl.appendChild(deleteBtn);
rightClickContainer.appendChild(rightClickUl);
rightClickContainer.classList.add("right-click");
app.appendChild(rightClickContainer);

document.onclick = () => {
    rightClickContainer.style.display = "none";
};

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

    const deleteFileOrFolder = async (type, id) => {
        if (type === "folder") {
            await fetch(`/api/folder/${id}`, {
                credentials: "include",
                method: "DELETE",
            });
        } else if (type === "file") {
            await fetch(`/api/file/${id}`, {
                credentials: "include",
                method: "DELETE",
            });
        }
    };

    const renameFileOrFolder = async (type, id, name) => {
        if (type === "folder") {
            await fetch(`/api/folder/rename/${id}?name=${name}`, {
                credentials: "include",
                method: "PUT",
            });
        } else if (type === "file") {
            await fetch(`/api/file/rename/${id}?name=${name}`, {
                credentials: "include",
                method: "PUT",
            });
        }
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
                folderPath.push(folder);
                await getFilesAndFolders();
                renderFilesAndFolders();
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
            folderElement.oncontextmenu = function (e) {
                e.preventDefault();
                rightClickContainer.style.display = "block";
                rightClickContainer.style.left = Number(e.pageX) + 1 + "px";
                rightClickContainer.style.top = Number(e.pageY) + 1 + "px";

                renameBtn.onclick = async () => {
                    // create modal
                    let newName = folder.name;
                    const renameModal = document.createElement("div");
                    renameModal.classList.add("rename-modal");
                    const renameInput = document.createElement("input");
                    renameInput.value = newName;
                    renameInput.onchange = (e) => {
                        newName = e.target.value;
                        renameInput.value = e.target.value;
                    };
                    renameInput.placeholder = "New name";
                    renameInput.onclick = (e) => {
                        e.stopPropagation();
                    };
                    const renameBtn = document.createElement("button");
                    renameBtn.innerHTML = "Rename";

                    const close = () => {
                        renameBtn.removeEventListener("click", rename);
                        app.removeChild(renameModal);
                    };

                    const rename = async (e) => {
                        e.stopPropagation();
                        await renameFileOrFolder("folder", folder.id, newName);
                        await getFilesAndFolders();
                        renderFilesAndFolders();
                        close();
                    };
                    renameBtn.addEventListener("click", rename);
                    renameModal.appendChild(renameInput);
                    renameModal.appendChild(renameBtn);
                    app.appendChild(renameModal);
                    renameModal.onclick = close;
                };

                deleteBtn.onclick = async () => {
                    await deleteFileOrFolder("folder", folder.id);
                    await getFilesAndFolders();
                    renderFilesAndFolders();
                };
            };
            filesElement.appendChild(folderElement);
        });
        files.forEach((file) => {
            const fileExtension = file.name.split(".").pop().toLowerCase();
            const fileIcon = fileIconMap[fileExtension] || "file";
            // add to DOM
            const fileElement = document.createElement("div");
            fileElement.classList.add("file");
            fileElement.onclick = async () => {
                window.open(file.url, "_blank");
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
                const fileSizeElement = document.createElement("p");
                let size = file.size;
                if (size < 1024) {
                    size += " bytes";
                } else if (size < Math.pow(1024, 2)) {
                    size = (size / 1024).toFixed(1) + " KB";
                } else if (size < Math.pow(1024, 3)) {
                    size = (size / Math.pow(1024, 2)).toFixed(1) + " MB";
                } else if (size < Math.pow(1024, 4)) {
                    size = (size / Math.pow(1024, 3)).toFixed(1) + " GB";
                } else {
                    size = (size / Math.pow(1024, 4)).toFixed(1) + " TB";
                }
                fileSizeElement.innerHTML = size;
                fileElement.appendChild(fileSizeElement);
            }
            fileElement.oncontextmenu = function (e) {
                e.preventDefault();
                rightClickContainer.style.display = "block";
                rightClickContainer.style.left = Number(e.pageX) + 1 + "px";
                rightClickContainer.style.top = Number(e.pageY) + 1 + "px";

                renameBtn.onclick = async () => {
                    // create modal
                    let newName = file.name;
                    const renameModal = document.createElement("div");
                    renameModal.classList.add("rename-modal");
                    const renameInput = document.createElement("input");
                    renameInput.value = newName;
                    renameInput.onchange = (e) => {
                        newName = e.target.value;
                        renameInput.value = e.target.value;
                    };
                    renameInput.placeholder = "New name";
                    renameInput.onclick = (e) => {
                        e.stopPropagation();
                    };
                    const renameBtn = document.createElement("button");
                    renameBtn.innerHTML = "Rename";

                    const close = () => {
                        renameBtn.removeEventListener("click", rename);
                        app.removeChild(renameModal);
                    };

                    const rename = async (e) => {
                        e.stopPropagation();
                        await renameFileOrFolder("file", file.id, newName);
                        await getFilesAndFolders();
                        renderFilesAndFolders();
                        close();
                    };
                    renameBtn.addEventListener("click", rename);
                    renameModal.appendChild(renameInput);
                    renameModal.appendChild(renameBtn);
                    app.appendChild(renameModal);
                    renameModal.onclick = close;
                };

                deleteBtn.onclick = async () => {
                    await deleteFileOrFolder("file", file.id);
                    await getFilesAndFolders();
                    renderFilesAndFolders();
                };
            };
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
                loginLogoutElement.style.height = "fit-content";
                document.querySelector(".back-btn").style.display = "block";
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
                loginLogoutElement.style.height = "100vh";
                document.querySelector(".back-btn").style.display = "none";
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
        // upload file
        while (uploadContainer.firstChild) {
            uploadContainer.removeChild(uploadContainer.firstChild);
        }
        if (auth) {
            const newButton = document.createElement("div");
            newButton.classList.add("new-btn");
            const downIcon = materialIcon("expand_more");
            newButton.innerHTML = "New";
            newButton.appendChild(downIcon);
            newButton.onclick = function () {
                if (newButton.classList.contains("opened")) {
                    newButton.classList.remove("opened");
                } else {
                    newButton.classList.add("opened");
                }
            };
            uploadContainer.appendChild(newButton);

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
    };

    const reload = async () => {
        await Promise.all([authRelateRender(), getFilesAndFolders()]);
        renderFilesAndFolders();
    };
    await reload();
})();
