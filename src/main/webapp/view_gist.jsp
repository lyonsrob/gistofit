<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML>
<!--
	Miniport by HTML5 UP
	html5up.net | @n33co
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
-->
<html lang="en" >
	<head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# >
		<title>${ extract.title } | GistOfIt</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		
		<!-- Facebook Tags --> 		
		<meta property="fb:app_id" content="280011745515851" /> 
		<meta property="og:url"    content="http://gistof.it/gist/${ gist.id}" /> 
		<meta property="og:type"   content="article" /> 
		<meta property="og:title"  content="${ extract.title }" /> 
		<meta property="og:image"  content="${ extract.images[0].url }" /> 
		<meta property="og:image:width"  content="${ extract.images[0].width }" /> 
		<meta property="og:image:height"  content="${ extract.images[0].height }" /> 
	        <meta property="og:description" content="${ extract.description }" />	
		
		<!-- meta property="article:section" content="Summary" />
		<meta property="article:publisher" content="${ extract.providerName }" / -->
		
		<!-- Twitter Tags --> 
		<meta name="twitter:card" content="summary_large_image" />
		<meta name="twitter:site" content="@gistof_it" />
		<meta name="twitter:creator" content="${ extract.providerName }" />
		

		<!--[if lte IE 8]><script src="../css/ie/html5shiv.js"></script><![endif]-->
		<script src="js/jquery.min.js"></script>
		<script src="js/jquery.scrolly.min.js"></script>
		<script src="js/skel.min.js"></script>
		<script src="js/init.js"></script>
		<link href='//fonts.googleapis.com/css?family=Open+Sans:700,300,400' rel='stylesheet' type='text/css'>
  		<link rel="stylesheet" href="css/supersonic.css" />
	
		<style>
			@media (min-width: 1200px) {
			  .list {
			  	width: 75%; 
				margin: 0 25% 0 12%;
			  }
			}
		</style>	
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
	<body class="overflow-scroll scroll-content">
<nav id="nav">
				<ul class="container">
					<li><a href="/#work">meet the team</a></li>
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
			    <article target-blank id="first" class="container">
				<div class="list card">
					  <div class="item item-avatar" style="border:none;">
					    <img src="${ gist.user.profilePicture }">
					    <h4>by ${gist.user.firstName} &#183; <span am-time-ago="gist.date"></span></h4>
					    <img style="vertical-align: middle;" src="${ extract.faviconUrl }" width="16px" height="16px" /><h5 style="display:inline; vertical-align:middle;"> via ${extract.providerName}</h5>
					  </div>
					
				<div class="item item-body item-text-wrap" style="border:none;" >
				    <h2>
					#GistOfIt: ${gist.content}
				    </h2>
				</div>

				  <div class="item" style="border:none;" >
					<img class="full-image" src="${ extract.images[0].url }" href="#" />
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
	</body>
</html>
