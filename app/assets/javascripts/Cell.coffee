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
  constructor: (@cellName, @dna, @initialEnergy, position, @scene) ->
    @color = new THREE.Color()
    @sprite = new THREE.Mesh( circleGeo, @createMaterial() )
    @sprite.position.x = position.x
    @sprite.position.y = position.y
    @scene.add(@sprite)

  update: (energy, position) ->
    @color.setRGB(@dna.r, @dna.g, @dna.b)
    @adjustSizeToEnergy(energy)
    @sprite.position.set(position.x, position.y, 0 )


  adjustSizeToEnergy: (energy) ->
    @sprite.scale.x = energy/@initialEnergy
    @sprite.scale.y = energy/@initialEnergy

  createMaterial: () ->
    tex = circleTexture.clone()
    tex.needsUpdate = true
    @color.setRGB(@dna.r, @dna.g, @dna.b)
    new THREE.MeshBasicMaterial( {map: tex, color : @color} )

