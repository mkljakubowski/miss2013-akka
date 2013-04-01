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
      @cells[cellName] = new Cell(cellName, data.dna, data.energy, position, @scene)

    if data.type == "TargetDna"
      @targetDna = new TargetDna(data.dna,@scene)

    if data.type == "Unregister"
#      alert(@cells[cellName])
      @cells[cellName].removeFromScene()
      delete @cells[cellName]
