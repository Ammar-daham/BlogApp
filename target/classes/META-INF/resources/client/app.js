(function() {

	var posts = [];
	var users = [];

	function renderBlogPost(post) {
		var cardWrapper = $('<div class="card-warpper"></div>');
		var card = $('<div class="card"></div>');
		var cardBody = $('<div class="card-body"></div>');
		var cardTitle = $('<h5 class="card-title"></div>');
		var cardText = $('<p class="card-text"></p>');
		var viewLink = $('<a href="#" class="card-link">View this post</a>');
		var deleteLink = $('<a href="#" class="card-link">Delete</a>');
		cardTitle.text(post.title);
		cardText.text(new Date(post.date).toLocaleDateString());
		cardBody.append(cardTitle, cardText, viewLink, deleteLink).appendTo(card);
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
		deletePost(id, function() {
			var filtered = [];
			$.each(posts, function(index, post) {
				if ( post.id != id ) {
					filtered.push(post);
				}
			});
			posts = filtered;
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

	function deletePost(id, callback) {
		$.ajax({
			url: '/posts/' + id,
			dataType: 'json',
			type: 'DELETE',
			timeout: 5 * 60 * 1000,
			success: callback,
			error: function(e) {
				console.log(e);
			}
		});
	}

	$('#showAllPosts').click(function() {
		$('#postsContainer').removeClass('d-none');
		$('#postContainer').addClass('d-none');
	});

	$('#saveBlogPost').click(function() {
		var post = {
			title: $('#blogTitle').val(),
			body: $('#blogBody').val(),
			date: Date.now()
		};
		$('#newPost').modal('hide');
		savePost(post, function(data) {
			posts.push(data);
			renderAllPosts();
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
			date: Date.now()
		};
		$('#register').modal('hide');
		createUser(user, function(data) {
			users.push(data);
			//renderAllPosts();
		})
	});

})();
