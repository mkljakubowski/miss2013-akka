sqaureTextures = [
  THREE.ImageUtils.loadTexture("/assets/images/square0.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square1.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square2.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square3.png"),
]
class Cell
  constructor: (@cellName, @dna, @radius, @position, @scene) ->
#    @geometry   = new THREE.Geometry
#    @material   = new ParticleMaterial().generate('0xff33ff')
#    @vertices   = @geometry.vertices               = [new THREE.Vector3(@position.x, @position.y, 0)]
#    @alpha      = @material.attributes.alpha.value = [0.0]
#    @size       = @material.attributes.size.value  = [4.0]
#    @particles  = new THREE.ParticleSystem(@geometry, @material)
#    @scene.add(@particles)
    @sprite = new THREE.Sprite( { map: sqaureTextures[1], useScreenCoordinates: false, alignment: THREE.SpriteAlignment.center } );
    @sprite.position.set(@position.x, @position.y, 0 )
    @sprite.scale.set(1, 1, 1)
    @sprite.opacity = 0.7
    @scene.add(@sprite)
    console.log(@sprite)
#    show on scene

  update: (dna, radius, position) ->
    @sprite.position.set(position.x, position.y, 0 )