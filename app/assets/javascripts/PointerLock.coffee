class PointerLock
  constructor: (onLock, onUnlock, onFailue) ->
    @havePointerLock = 'pointerLockElement' of document ||'mozPointerLockElement' of document || 'webkitPointerLockElement' of document
    @isLocked = false

    if @havePointerLock
      pointerLockChange = =>
        body = document.body
        if document.pointerLockElement == body || document.mozPointerLockElement == body || document.webkitPointerLockElement == body
          @isLocked = true
          onLock()
        else
          @isLocked = false
          onUnlock()

      document.addEventListener('pointerlockchange', pointerLockChange, false)
      document.addEventListener('mozpointerlockchange', pointerLockChange, false)
      document.addEventListener('webkitpointerlockchange', pointerLockChange, false)
    else
      onFailue()

  lockPointer: ->
    if @havePointerLock
      body = document.body
      body.requestPointerLock = body.requestPointerLock || body.mozRequestPointerLock || body.webkitRequestPointerLock
      body.requestFullscreen =  body.requestFullscreen ||  body.mozRequestFullscreen || body.mozRequestFullScreen || body.webkitRequestFullscreen

      if /Firefox/i.test(navigator.userAgent)
        fullscreenchange = ->
          if document.fullscreenElement == body || document.mozFullscreenElement == body || document.mozFullScreenElement == body
            document.removeEventListener('fullscreenchange', fullscreenchange)
            document.removeEventListener('mozfullscreenchange', fullscreenchange)
            body.requestPointerLock()

        document.addEventListener('fullscreenchange', fullscreenchange, false)
        document.addEventListener('mozfullscreenchange', fullscreenchange, false)
        body.requestFullscreen()
      else
        body.requestPointerLock()

  onMouseMove: (minOffset, maxOffset, callback) ->
    pos = new THREE.Vector3(0, 0, 0)
    mov = new THREE.Vector3(0, 0, 0)
    $(document).mousemove =>
      if @isLocked
        mov.addSelf(new THREE.Vector3(
          (event.movementX || event.mozMovementX || event.webkitMovementX || 0) / 10,
          -(event.movementY || event.mozMovementY || event.webkitMovementY || 0) / 10,
          0
        ))

        if mov.length() > maxOffset
          mov.normalize().multiplyScalar(maxOffset)

        if mov.length() > minOffset
          callback(pos.addSelf(mov).clone())
          mov.set(0, 0, 0)
