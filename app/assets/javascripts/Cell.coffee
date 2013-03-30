sqaureTextures = [
  THREE.ImageUtils.loadTexture("/assets/images/square0.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square1.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square2.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square3.png"),
]

canvas = document.createElement( 'canvas' )
canvas.width = 32
canvas.height = 32
context = canvas.getContext( '2d' )
context.beginPath()
context.arc(16, 16, 16, 0, 2 * Math.PI, false)
context.fillStyle = 'white'
context.fill()
context.lineWidth = 0
context.stroke()
circleTexture = new THREE.Texture( canvas )
circleTexture.needsUpdate = true
circleMaterial = new THREE.MeshBasicMaterial( {map: circleTexture} );
circleGeo = new THREE.CircleGeometry( 1, 10, 0, 2 * Math.PI )

class Cell
  constructor: (@cellName, @dna, radius, position, @scene) ->
    @sprite = new THREE.Mesh( circleGeo, circleMaterial )
    @sprite.position.x = position.x
    @sprite.position.y = position.y
    @scene.add(@sprite)
#    show on scene

  update: (dna, radius, position) ->
    @sprite.position.set(position.x, position.y, 0 )