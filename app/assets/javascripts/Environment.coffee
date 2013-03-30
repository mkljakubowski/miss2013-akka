class Environment
  constructor: (@scene) ->
    @cells = {}
    @layer = new Kinetic.Layer()
    @scene.add(@layer);
    ala123 = -> @scene.draw()
    window.setInterval(ala123, 1000)

  hexColor: (dna)->
    r = ('0'+(Math.random()*256|0).toString(16)).slice(-2)
    g = ('0'+(Math.random()*256|0).toString(16)).slice(-2)
    b = ('0'+(Math.random()*256|0).toString(16)).slice(-2)
    r + g + b

  updateFromServer: (data) =>
    cellName = data.cellName

    if data.type == "UpdateCell"
#      position = new THREE.Vector3(data.x, data.y, 0)

      if cellName not of @cells
        @cells[cellName] = new Cell(cellName, data.dna, data.r, data.x, data.y, @scene, @layer)

      if cellName of @cells
        @cells[cellName].update(data.dna, data.r, data.x, data.y)
