
# For an explanation of the steroids.config properties, see the guide at
# http://guides.appgyver.com/steroids/guides/project_configuration/config-application-coffee/

steroids.config.name = "GistOfIt"

# ## Start Location
steroids.config.location = "http://localhost/views/Current/current.html"

# ## Tab Bar
steroids.config.tabBar.enabled = true
steroids.config.tabBar.tabs = [
  {
    title: "Current"
    icon: "icons/ios7-world.png"
    location: "http://localhost/views/Current/current.html"
  },
  {
    title: "Trending"
    icon: "icons/arrow-graph-up-right.png"
    location: "http://localhost/views/Trending/trending.html"
  },
  {
    title: "Feed"
    icon: "icons/ios7-albums.png"
    location: "http://localhost/views/Feed/feed.html"
  },
  {
    title: "Profile"
    icon: "icons/ios7-world.png"
    location: "http://localhost/views/Profile/profile.html"
  },
]
steroids.config.tabBar.tintColor = "ffffff"
steroids.config.tabBar.tabTitleColor = "969696"
steroids.config.tabBar.selectedTabTintColor = "60ab00"
steroids.config.tabBar.selectedTabBackgroundImage = ""

steroids.config.tabBar.backgroundImage = ""
      

## Preloads
steroids.config.preloads = [
  {
    id: "search"
    location: "http://localhost/views/Search/search.html",
  },
  {
    id: "comments"
    location: "http://localhost/views/Comments/comments.html"
  },
  {
    id: "article"
    location: "http://localhost/views/Article/article.html"
  },
  {
    id: "addGist"
    location: "http://localhost/views/Gist/add.html"
  }
]
    
# -- Drawers
steroids.config.drawers =
  left:
    id: "sidemenu"
    location: "http://localhost/views/sidemenu/sidemenu.html"
    showOnAppLoad: false
    widthOfDrawerInPixels: 250
  options:
    centerViewInteractionMode: "None"
    closeGestures: ["PanNavBar", "PanCenterView", "TapCenterView", "TapNavBar"]
    openGestures: ["PanNavBar", "PanCenterView"]
    showShadow: true
    stretchDrawer: true
    widthOfLayerInPixels: 0

# ## Initial View
# steroids.config.initialView =
#   id: "initialView"
#   location: "http://localhost/initialView.html"

# ## Navigation Bar
steroids.config.navigationBar.tintColor = "60ab00"
steroids.config.navigationBar.titleColor = "ffffff"

steroids.config.navigationBar.buttonTintColor = "ffffff"
steroids.config.navigationBar.buttonTitleColor = "ffffff"

# steroids.config.navigationBar.borderColor = "#000000"
# steroids.config.navigationBar.borderSize = 2

# steroids.config.navigationBar.landscape.backgroundImage = ""
# steroids.config.navigationBar.portrait.backgroundImage = ""

# ## Android Loading Screen
steroids.config.loadingScreen.tintColor = "#262626"

# ## iOS Status Bar
steroids.config.statusBar.enabled = true
steroids.config.statusBar.style = "light"

# ## File Watcher
# steroids.config.watch.exclude = ["www/my_excluded_file.js", "www/my_excluded_dir"]

# ## Pre- and Post-Make Hooks
# steroids.config.hooks.preMake.cmd = "echo"
# steroids.config.hooks.preMake.args = ["running yeoman"]
# steroids.config.hooks.postMake.cmd = "echo"
# steroids.config.hooks.postMake.args = ["cleaning up files"]

# ## Default Editor
# steroids.config.editor.cmd = "subl"
# steroids.config.editor.args = ["."]
  
