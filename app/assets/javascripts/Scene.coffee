class Scene
  constructor: ->
#    if !Detector.webgl then Detector.addGetWebGLMessage()

#    @scene = new THREE.Scene()
#    @scene.fog = new THREE.FogExp2(0x000000, 0.0005)

#    @renderer = new THREE.WebGLRenderer(clearColor: 0x66CCFF, clearAlpha: 1, antialias: false)
#    @renderer.setSize(window.innerWidth, window.innerHeight)


#    @camera = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight, 1, 10000)
#    @camera.position.z = 100

#    @stats = new Stats()
#    @stats.domElement.style.position = 'absolute'
#    @stats.domElement.style.top      = '5px'
#    @stats.domElement.style.left     = '5px'
#    @canvas = document.getElementById('cellCanvas');
#    @canvas.setAttribute("width", window.innerWidth)
#    @canvas.setAttribute("height", window.innerHeight)
#    @canvas.setAttribute("style", "background: #b3d4fc")
    @stage = new Kinetic.Stage({
      container: 'container',
      width: window.innerWidth,
      height: window.innerHeight
    })

    pointerLock = new PointerLock(
      () => blocker.style.display  = 'none'
      () => blocker.style.display  = 'block'
      () => instructions.innerHTML = 'Your browser doesn\'t seem to support Pointer Lock API'
    )

    @environment = new Environment(@stage)

    @ws = new Websocket((e) =>
      data = JSON.parse(e.data)
      @environment.updateFromServer(data)
    )

#    pointerLock.onMouseMove(@particleSize, @particleSize * 1.5, @ws.send)

    instructions.addEventListener('click', (event) => pointerLock.lockPointer())

#    document.body.appendChild(@renderer.domElement)
#    document.body.appendChild(@stats.domElement)

#  animate: =>
#    requestAnimationFrame(@animate)
#    @renderer.render(@scene, @camera)
#    @stats.update()
#    @players.update()