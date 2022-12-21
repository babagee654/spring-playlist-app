function verify() {

	
	var password1 = document.forms['sign-up']['password'].value;
	var password2 = document.forms['sign-up']['verify-password'].value;
	if (password1 == null || password1 == "" || password1 != password2) {
		
		// Prevent recreating div on multiple fail attempts.
		if (!document.querySelector("#error")){
			let error = document.createElement("div");
			error.id = "error";
			error.classList.add("my-2","p-2","param");
			error.innerHTML = "Please ensure passwords are the same";
			
			let mainContainer = document.querySelector(".main-container");
			
			mainContainer.insertBefore(error, mainContainer.firstElementChild)
		}
		
		return false;
	}
	
	return true;
}

