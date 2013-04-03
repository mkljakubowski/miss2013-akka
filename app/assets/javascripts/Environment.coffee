class Environment
  constructor: (@scene) ->
    @cells = {}

  updateFromServer: (data) =>
    cellName = data.cellName

    if data.type == "UpdateCell"
      position = new THREE.Vector3(data.x, data.y, 0)

      if cellName of @cells
        @cells[cellName].update(data.energy, position, data.dna)

    if data.type == "Register"
      position = new THREE.Vector3(data.x, data.y, 0)
      @cells[cellName] = new Cell(cellName, data.dna, 70, position, @scene)

    if data.type == "TargetDna"
#      @targetDna = new TargetDna(data.dna,@scene)
      console.log(data.dna)
      cc = new THREE.Color()
      cc.setRGB(data.dna.r, data.dna.g, data.dna.b)
      scene.renderer.setClearColor(cc, 1)

    if data.type == "Unregister"
#      alert(@cells[cellName])
      @cells[cellName].removeFromScene()
      delete @cells[cellName]
