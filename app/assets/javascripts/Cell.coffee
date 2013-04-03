
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
  constructor: (@cellName, @dna, energy, position, @scene) ->
    @color = new THREE.Color()
    @sprite = new THREE.Mesh( circleGeo, @createMaterial() )
    @sprite.position.x = position.x
    @sprite.position.y = position.y
    @adjustSizeToEnergy(energy)
    @scene.add(@sprite)

  update: (energy, position, @dna) ->
    @adjustSizeToEnergy(energy)
    @sprite.position.set(position.x, position.y, 0 )


  adjustSizeToEnergy: (energy) ->
    scale = (energy/40)
    console.log(scale) if scale > 2.5
    @sprite.scale.set(scale, scale, 1)

  removeFromScene: () ->
     @scene.remove(@sprite)

  createMaterial: () ->
    tex = circleTexture.clone()
    tex.needsUpdate = true
    @color.setRGB(@dna.r, @dna.g, @dna.b)
    new THREE.MeshBasicMaterial( {map: tex, color : @color} )

