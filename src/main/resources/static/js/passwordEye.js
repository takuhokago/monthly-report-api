function pushHideButton() {
            var txtPass = document.getElementById("password");
            var btnEye = document.getElementById("buttonEye");
            var btnEyeSlash = document.getElementById("buttonEyeSlash");
            if (txtPass.type === "text") {
                txtPass.type = "password";
                btnEye.style.display = "none";
                btnEyeSlash.style.display = ""
            } else {
                txtPass.type = "text";
                btnEyeSlash.style.display = "none";
                btnEye.style.display = ""
            }
        }