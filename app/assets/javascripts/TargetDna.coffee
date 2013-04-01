#canvas = document.createElement( 'canvas' )
#canvas.width = 32
#canvas.height = 32
#context = canvas.getContext( '2d' )
#context.beginPath()
#context.arc(16, 16, 16, 0, 2 * Math.PI, false)
#context.fillStyle = 'white'
#context.fill()
#context.lineWidth = 0
#context.stroke()
#circleTexture = new THREE.Texture( canvas )
dnaCircleGeo = new THREE.CircleGeometry( 10, 10, 0, 2 * Math.PI )

class TargetDna
  constructor: (@dna, @scene) ->

    @color = new THREE.Color()
    @sprite = new THREE.Mesh( dnaCircleGeo, @createMaterial() )
    @sprite.position.x = 0
    @sprite.position.y = 0
    @scene.add(@sprite)

  createMaterial: () ->
    tex = circleTexture.clone()
    tex.needsUpdate = true
    @color.setRGB(@dna.r, @dna.g, @dna.b)
    new THREE.MeshBasicMaterial( {map: tex, color : @color} )

