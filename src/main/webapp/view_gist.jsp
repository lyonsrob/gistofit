<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML>
<!--
	Miniport by HTML5 UP
	html5up.net | @n33co
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
-->
<html lang="en" >
	<head>
		<title>Miniport by HTML5 UP</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<!--[if lte IE 8]><script src="../css/ie/html5shiv.js"></script><![endif]-->
		<script src="js/jquery.min.js"></script>
		<script src="js/jquery.scrolly.min.js"></script>
		<script src="js/skel.min.js"></script>
		<script src="js/init.js"></script>
		<link href='//fonts.googleapis.com/css?family=Open+Sans:700,300,400' rel='stylesheet' type='text/css'>
		
		<noscript>
			<link rel="stylesheet" href="css/skel.css" />
			<link rel="stylesheet" href="css/style.css" />
			<link rel="stylesheet" href="css/style-desktop.css" />
		</noscript>
		<!--[if lte IE 8]><link rel="stylesheet" href="css/ie/v8.css" /><![endif]-->
		<!--[if lte IE 9]><link rel="stylesheet" href="css/ie/v9.css" /><![endif]-->

		<script>

		 	var count = 0;
		 	setInterval( highlight, 2000);
			function highlight() {
				    $('#read').css("font-weight", "400");
				    $('#write').css("font-weight", "400");
				    $('#repeat').css("font-weight", "400");

				    if (count == 0) {
				    	$('#read').css("font-weight", "800");
					}
				    
				    if (count == 1) {
				    	$('#write').css("font-weight", "800");
					}
				    
				    if (count == 2) {
				    	$('#repeat').css("font-weight", "800");
				    	count = 0;
				    	return;
					}
				      
				    count = count+1; 
			}
		</script>

	</head>
	<body>
<nav id="nav">
				<ul class="container">
					<li><a href="#top">Demo</a></li>
					<li><a href="#work">Team</a></li>
				</ul>
			</nav>
		<!-- Home -->
			<div class="wrapper style1 first">
				<article class="container" id="top">
					<div class="row">
						<div class="12u">
						  <center>
							<header>
								<img width="250px" src="/images/logo.png" />
								<h5>The Internet. <span class="headline-green">Summarized</span>.</h5>
							</header>
							<a><img alt="Download on the App Store" src="images/Download_on_the_App_Store_Badge_US-UK_135x40.svg"></a>
						  </center>
					</div>
				</article>
			</div>
		<!-- Main -->
		<!-- One -->
		<section id="one">
			<!-- Feature 1 -->
			    <article target-blank id="first" class="container box style1 right">
			    	<h1>${gist.id}</h1>
			    	
					<div class="list card">
					  <div class="item item-avatar">
					    <img src="${ extract.faviconUrl }">
					    <h2>via ${extract.providerName}</h2>
					  </div>
				
					  <div class="item">
						  <div class="item item-avatar">
						    <img src="${ gist.user.profilePicture }">
						    <p>
							#gistofit: ${gist.content}
						    </p>
						    <h4>by ${gist.user.firstName} · <span am-time-ago="gist.date"></span></h4>
						  </div>
						
						<img class="full-image" src="${ extract.images[0].url }" ng-click="openURL(gist.url.key.raw.name)" />
					    <h2>
					      ${extract.title}
					    </h2>
					    <div class="description">
					    <p>
					      ${extract.description}
					    </p>
					    </div>
					  </div>
			    </article>
		</section>


		<!-- Contact -->
			<div class="wrapper style4">
					<footer>
						<ul id="copyright">
							<li>&copy; Untitled. All rights reserved.</li><li>Design: <a href="http://html5up.net">HTML5 UP</a></li>
						</ul>
					</footer>
			</div>
<script>
		var header = document.querySelector('#main');
		var origOffsetY = header.offsetTop;
		
		function onScroll(e) {
		  window.scrollY >= origOffsetY ? header.classList.add('sticky') :
		                                  header.classList.remove('sticky');
		}
		
		document.addEventListener('scroll', onScroll);
</script>
	</body>
</html>
