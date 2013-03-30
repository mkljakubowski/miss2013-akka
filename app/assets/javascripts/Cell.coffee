#sqaureTextures = [
#  THREE.ImageUtils.loadTexture("/assets/images/square0.png"),
#  THREE.ImageUtils.loadTexture("/assets/images/square1.png"),
#  THREE.ImageUtils.loadTexture("/assets/images/square2.png"),
#  THREE.ImageUtils.loadTexture("/assets/images/square3.png"),
#]

class Cell
  constructor: (@cellName, @dna, @radius, @x, @y, @scene, @layer) ->
#    @sprite = new THREE.Sprite( { map: sqaureTextures[1], useScreenCoordinates: false, alignment: THREE.SpriteAlignment.center } );
#    @sprite.position.set(@position.x, @position.y, 0 )
#    @sprite.scale.set(1, 1, 1)
#    @sprite.opacity = 0.7
#    @scene.add(@sprite)
#    console.log(@sprite)
#    show on scene
#    console.log(@scene)

#    @context = @scene.getContext('2d')
#    @context.beginPath()
#    @context.arc(Math.floor(@x * 800 / @scene.width), Math.floor(@y * 450 / @scene.height), Math.floor(@radius), 0, 2 * Math.PI, false)
#    @context.fillStyle = 'green'
#    @context.fill()
#    @context.lineWidth = 1
#    @context.strokeStyle = '#003300'
#    @context.stroke()

    @circle = new Kinetic.Circle({
      x: @x * 800 / @scene.getWidth(),
      y: @y * 450 / @scene.getHeight(),
      radius: 70,
      fill: 'red',
      stroke: 'black',
      strokeWidth: 1
    });
    @layer.add(@circle);
    @scene.draw()

  update: (dna, radius, x, y) ->
#    @sprite.position.set(position.x, position.y, 0 )
    @circle.setPosition(x,y)
#    @scene.draw()