// Función para obtener las tareas desde la API
function fetchTasks() {
    fetch('/api/tasks') // Llamada a la API que devuelve las tareas
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al obtener las tareas');
            }
            return response.json();
        })
        .then(data => {
            renderTasks(data);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('No se pudieron obtener las tareas.');
        });
}

// Función para renderizar la lista de tareas en el DOM
function renderTasks(tasks) {
    const taskList = document.getElementById('taskList');
    taskList.innerHTML = ''; // Limpiar la lista antes de agregar las tareas

    if (tasks.length === 0) {
        taskList.innerHTML = '<li>No hay tareas disponibles.</li>';
    } else {
        tasks.forEach(task => {
            const listItem = document.createElement('li');
            listItem.classList.add('task-item');
            listItem.innerHTML = `
                <div class="task-content">
                    <span class="task-id"><strong>ID:</strong> ${task.id}</span>
                    <h3 class="task-title">${task.title}</h3>
                    <p class="task-description"><strong>Descripción:</strong> ${task.description}</p>
                    <p class="task-status"><strong>Estado:</strong> ${task.status}</p>
                    <p class="task-due-date"><strong>Fecha límite:</strong> ${task.dueDate}</p>
                </div>
                <div class="task-actions">
                    <button class="edit-btn" onclick="editTask('${task.id}')">Editar</button>
                    <button class="delete-btn" onclick="deleteTask('${task.id}')">Eliminar</button>
                </div>
            `;
            taskList.appendChild(listItem); // Añadir la tarea al DOM
        });
    }
}

// Función para eliminar una tarea (simulada)
function deleteTask(taskId) {
    if (!confirm('¿Estás seguro de que deseas eliminar esta tarea?')) {
        return;
    }

    // Simulación de eliminar tarea de la API
    fetch(`/api/tasks/${taskId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al eliminar la tarea.');
            }
            // Eliminar el elemento de la lista del DOM
            const taskList = document.getElementById('taskList');
            const listItem = taskList.querySelector(`button[onclick="deleteTask('${taskId}')"]`).parentElement.parentElement;
            taskList.removeChild(listItem);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('No se pudo eliminar la tarea.');
        });
}

// Simulación de función para editar una tarea
function editTask(taskId) {
    alert('Función de editar no implementada. ID de tarea: ' + taskId);
}

// Función para agregar una nueva tarea
function addTask() {
    const title = document.getElementById('taskTitle').value.trim();
    const description = document.getElementById('taskDescription').value.trim();
    const dueDate = document.getElementById('taskDueDate').value;

    // Validación simple
    if (title === '' || description === '' || dueDate === '') {
        alert('Todos los campos son obligatorios.');
        return;
    }

    // Crear el objeto de la nueva tarea
    const newTask = {
        title: title,
        description: description,
        status: 'PENDING', // Estado inicial como PENDING
        dueDate: dueDate
    };

    // Enviar la nueva tarea a la API con una solicitud POST
    fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newTask)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al agregar la tarea.');
            }
            return response.json();
        })
        .then(addedTask => {
            // Añadir la nueva tarea al listado en el DOM
            const taskList = document.getElementById('taskList');
            const listItem = document.createElement('li');
            listItem.classList.add('task-item');
            listItem.innerHTML = `
            <div class="task-content">
                <span class="task-id"><strong>ID:</strong> ${addedTask.id}</span>
                <h3 class="task-title">${addedTask.title}</h3>
                <p class="task-description"><strong>Descripción:</strong> ${addedTask.description}</p>
                <p class="task-status"><strong>Estado:</strong> ${addedTask.status}</p>
                <p class="task-due-date"><strong>Fecha límite:</strong> ${addedTask.dueDate}</p>
            </div>
            <div class="task-actions">
                <button class="edit-btn" onclick="editTask('${addedTask.id}')">Editar</button>
                <button class="delete-btn" onclick="deleteTask('${addedTask.id}')">Eliminar</button>
            </div>
        `;
            taskList.appendChild(listItem);

            // Limpiar el formulario
            document.getElementById('taskForm').reset();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('No se pudo agregar la tarea.');
        });
}


// Llamar a la función fetchTasks cuando se cargue la página
document.addEventListener('DOMContentLoaded', fetchTasks);
