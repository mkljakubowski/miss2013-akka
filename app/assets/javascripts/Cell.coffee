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
circleGeo = new THREE.CircleGeometry( 1, 10, 0, 2 * Math.PI )

class Cell
  constructor: (@cellName, @dna, radius, position, @scene) ->
    @color = new THREE.Color()
    @sprite = new THREE.Mesh( circleGeo, @createMatrial() )
    @sprite.position.x = position.x
    @sprite.position.y = position.y
    @scene.add(@sprite)

  update: (@dna, radius, position) ->
    @color.setRGB(@dna.r/255, @dna.g/255, @dna.b/255)
    @sprite.position.set(position.x, position.y, 0 )

  createMatrial: () ->
    tex = circleTexture.clone()
    tex.needsUpdate = true
    @color.setRGB(@dna.r/255, @dna.g/255, @dna.b/255)
    new THREE.MeshBasicMaterial( {map: tex, color : @color} )

