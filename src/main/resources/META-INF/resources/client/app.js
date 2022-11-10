(function() {

	var posts = [];
	var users = [];

	function renderBlogPost(post) {
		var cardWrapper = $('<div class="card-warpper"></div>');
		var card = $('<div class="card"></div>');
		var cardBody = $('<div class="card-body"></div>');
		var cardTitle = $('<h5 class="card-title"></div>');
		var cardText = $('<p class="card-text"></p>');
		var cardUsername = $('<p class="card-username"></p>');
		var viewLink = $('<a href="#" class="card-link">View this post</a>');
		var deleteLink = $('<a href="#" class="card-link">Delete</a>');
		cardTitle.text(post.title);
		cardText.text(new Date(post.date).toLocaleDateString());
		cardUsername.text("name: " + post.username);
		cardBody.append(cardTitle, cardText, cardUsername, viewLink, deleteLink).appendTo(card);
		card.appendTo(cardWrapper);
		viewLink.click(function() {
			viewPost(post);
		});
		deleteLink.click(function() {
			removePost(post.id);
		});
		return cardWrapper;
	}
	
	function renderAllPosts() {
		var container = $('#posts');
		container.empty();
		$.each(posts, function(index, post) {
			container.append(renderBlogPost(post));
		});
	}

	function viewPost(post) {
		$('#postsContainer').addClass('d-none');
		$('#postContainer').removeClass('d-none');
		$('#viewTitle').text(post.title);
		$('#viewTime').text(new Date(post.date));
		$('#viewBody').text(post.body);
	}

	   function removePost(id) {
	   		$('#postsContainer').addClass('d-none');
		   var payload = {
			   username: localStorage.getItem("username"),
			   id: id
		   };
	   		deletePost(payload, function() {
				   //console.log("username" + username)
				   	var filtered = [];
					$.each(posts, function (index, post) {
						if(post.id == id && post.username == payload.username) {
							console.log("deleted " + id );
						} else {
							filtered.push(post);
							console.log("You can not delete other user's post!")
						}
				});
			   posts = filtered;
			   console.log(posts);
			   renderAllPosts();
			   $('#postsContainer').removeClass('d-none');
			});
	   }

	function getPosts(callback) {
		$.ajax({
			url: '/posts',
			dataType: 'json',
			type: 'GET',
			timeout: 5 * 60 * 1000,
			success: callback
		});
	}

	function savePost(post, callback) {
		$.ajax({
			url: '/posts',
			dataType: 'json',
			contentType: 'application/json; charset=UTF-8',
			type: 'POST',
			data: JSON.stringify(post),
			timeout: 5 * 60 * 1000,
			success: callback
		});
	}

	function deletePost(payload, callback) {
		$.ajax({
			url: '/posts',
			dataType: 'json',
			type: 'DELETE',
			data: JSON.stringify(payload),
			timeout: 5 * 60 * 1000,
			success: callback,
			error: function(e) {
				$('error-message').innerText = e;
			}
		});
	}

	$('#showAllPosts').click(function() {
		$('#postsContainer').removeClass('d-none');
		$('#postContainer').addClass('d-none');
	});

	$('#saveBlogPost').click(function() {
		const username = localStorage.getItem("username");

		var post = {
			username: username,
			title: $('#blogTitle').val(),
			body: $('#blogBody').val(),
			date: Date.now(),
		};
		$('#newPost').modal('hide');
		savePost(post, function(data) {
			posts.push(data);
			renderAllPosts();
			console.log(posts);
			console.log(users);
		})
	});

	$('#newPost').on('shown.bs.modal', function() {
		$('#newPost .modal-body .form-control').val('');  
	});

	$('#register').on('shown.bs.modal', function() {
		$('#register .modal-body .form-control').val('');  
	});

	$(document).ready(function() {
		getPosts(function(data) {
			posts = data;
			renderAllPosts();
			$('#postsContainer').removeClass('d-none');
		});
	});

	function createUser(user, callback) {
		$.ajax({
			url: '/users',
			dataType: 'json',
			contentType: 'application/json; charset=UTF-8',
			type: 'POST',
			data: JSON.stringify(user),
			timeout: 5 * 60 * 1000,
			success: callback
		});
	}

	$('#register-btn').click(function() {
		var user = {
			username: $('#username').val(),
			password: $('#password').val(),
			date: Date.now(),
		};
		var paragraph = document.getElementById('register-message');
		var pw1 = $('#password').val();  
		var pw2 = $('#confirmPassword').val(); 
		if(pw1 === pw2 && user.username.length != 0 && user.password.length != 0) {
			createUser(user, function(data) {
				//users.push(data);
				paragraph.style.color = "green";
				paragraph.innerHTML = `Successfully registered, welcome ${data.username}`;
				setTimeout(redirectToHomePage ,1000);
				function redirectToHomePage(){
					$('#register').modal('hide');
				}
			})
		} else if(pw1 !== pw2) {
			paragraph.style.color = "red";
			paragraph.innerHTML = `Password did not match!`;
		} else if (user.username.length == 0 || user.password.length == 0) {
			paragraph.style.color = "red";
			paragraph.innerHTML = `username and password required!`;
		}		
	});

	function login(user, callback) {
		$.ajax({
			url: '/users/login',
			dataType: 'json',
			contentType: 'application/json; charset=UTF-8',
			type: 'POST',
			data: JSON.stringify(user),
			timeout: 5 * 60 * 1000,
			success: callback
		});
	}

	$('#login-btn').click(function() {
		var user = {
			username: $('#username-login').val(),
			password: $('#pws').val(),
		};

		var paragraph = document.getElementById("message");
		
		if(user.username.length != 0 && user.password.length != 0) {
			login(user, function(data) {
				if(data.isLoggedIn) {
					user.isLoggedIn = data.isLoggedIn;
					console.log(user)
					users.push(user);
					console.log(users)
					localStorage.setItem("username", user.username);
					paragraph.style.color = "green";
					$("#message").text(`Successfully logged In, welcome ${data.username}`);
					setTimeout(redirectToHomePage ,1000);
					function redirectToHomePage(){
						console.log(users)
						location.href = 'home.html';
					}
				} else if(data.error){
					console.log("username or password wrong, please try again!")
					paragraph.style.color = "red";
					$("#message").text("username or password wrong, please try again!");
				}
			})
		} else {
			paragraph.style.color = "red";
			$("#message").text("username and password required!");
		}
	});

	function logout(payload, callback) {
		$.ajax({
			url: '/users/logout',
			dataType: 'json',
			contentType: 'application/json; charset=UTF-8',
			type: 'POST',
			data: JSON.stringify(payload),
			timeout: 5 * 60 * 1000,
			success: callback
		});
	}

	$('#logout-btn').click(function() {
		console.log("logged out")
		const username = localStorage.getItem("username");
		var payload = {
			username: username
		}
		logout(payload, function (data) {
			  location.href="index.html"
		});
	})


})();
