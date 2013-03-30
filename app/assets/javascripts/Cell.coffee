sqaureTextures = [
  THREE.ImageUtils.loadTexture("/assets/images/square0.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square1.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square2.png"),
  THREE.ImageUtils.loadTexture("/assets/images/square3.png"),
]
class Cell
  constructor: (@cellName, @dna, radius, position, @scene) ->
    @sprite = new THREE.Sprite( { map: sqaureTextures[1], useScreenCoordinates: false, alignment: THREE.SpriteAlignment.center } );
    console.log(@position)
    @sprite.position.set(position.x, position.y, 0)
    @sprite.scale.set(4, 4, 4)
    @sprite.opacity = 1
    @scene.add(@sprite)
    console.log(@sprite)
#    show on scene

  update: (dna, radius, position) ->
    @sprite.position.set(position.x, position.y, 0 )