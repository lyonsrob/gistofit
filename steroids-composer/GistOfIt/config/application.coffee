
# For an explanation of the steroids.config properties, see the guide at
# http://guides.appgyver.com/steroids/guides/project_configuration/config-application-coffee/

steroids.config.name = "GistOfIt"

# ## Start Location
steroids.config.location = "http://localhost/views/Current/index.html"

# ## Tab Bar
steroids.config.tabBar.enabled = true
steroids.config.tabBar.tabs = [
  {
    title: "Current"
    icon: "icons/53.png"
    location: "http://localhost/views/Current/index.html"
  },
  {
    title: "Trending"
    icon: "icons/16.png"
    location: "http://localhost/views/Trending/index.html"
  },
  {
    title: "Feed"
    icon: "icons/34.png"
    location: "http://localhost/views/Feed/index.html"
  }
]
steroids.config.tabBar.tintColor = "a3c971"
steroids.config.tabBar.tabTitleColor = "ffffff"
steroids.config.tabBar.selectedTabTintColor = "5a5a54"
steroids.config.tabBar.selectedTabBackgroundImage = ""

steroids.config.tabBar.backgroundImage = ""
      

## Preloads
#steroids.config.preloads = [
#  {
#    id: "leftDrawer"
#    location: "http://localhost/views/Drawers/left.html"
#  }
#]
    
# -- Drawers
steroids.config.drawers =
  left:
    id: "sidemenu"
    location: "http://localhost/views/sidemenu/index.html"
    showOnAppLoad: false
    widthOfDrawerInPixels: 250
  options:
    centerViewInteractionMode: "Full"
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
steroids.config.navigationBar.tintColor = "a3c971"
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
steroids.config.statusBar.style = "default"

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
  
