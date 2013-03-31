class Environment
  constructor: (@scene) ->
    @cells = {}

  updateFromServer: (data) =>
    cellName = data.cellName

    if data.type == "UpdateCell"
      position = new THREE.Vector3(data.x, data.y, 0)

      if cellName of @cells
        @cells[cellName].update(data.energy, position)

    if data.type == "Register"
      position = new THREE.Vector3(data.x, data.y, 0)
      @cells[cellName] = new Cell(cellName, data.dna, data.energy, position, @scene)

    if data.type == "Unregister"
      delete @cells[cellName]
