// Fetch tasks from API and display them
function fetchTasks() {
    fetch('/api/tasks') // API call to get tasks
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch tasks');
            return response.json();
        })
        .then(renderTasks) // Render tasks after fetching
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to fetch tasks.');
        });
}

// Render the list of tasks in the DOM
function renderTasks(tasks) {
    const taskList = document.getElementById('taskList');
    taskList.innerHTML = ''; // Clear task list

    if (tasks.length === 0) {
        taskList.innerHTML = '<li>No tasks available.</li>';
    } else {
        tasks.forEach(task => {
            const listItem = document.createElement('li');
            listItem.classList.add('task-item');
            listItem.innerHTML = `
                <div class="task-content">
                    <span class="task-id"><strong>ID:</strong> ${task.id}</span>
                    <h3 class="task-title">${task.title}</h3>
                    <p class="task-description"><strong>Description:</strong> ${task.description}</p>
                    <p class="task-status"><strong>Status:</strong> ${task.status}</p>
                    <p class="task-due-date"><strong>Due Date:</strong> ${task.dueDate}</p>
                </div>
                <div class="task-actions">
                    <button class="edit-btn" onclick="editTask('${task.id}')">Edit</button>
                    <button class="delete-btn" onclick="deleteTask('${task.id}')">Delete</button>
                </div>
            `;
            taskList.appendChild(listItem); // Add task to the list
        });
    }
}

// Delete a task
function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) return;

    // Simulated API call to delete a task
    fetch(`/api/tasks/${taskId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to delete task');
            const taskList = document.getElementById('taskList');
            const listItem = taskList.querySelector(`button[onclick="deleteTask('${taskId}')"]`).parentElement.parentElement;
            taskList.removeChild(listItem); // Remove task from DOM
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to delete task.');
        });
}

// Edit a task
function editTask(taskId) {
    // Fetch task details to pre-fill the form for editing
    fetch(`/api/tasks/${taskId}`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch task data');
            return response.json();
        })
        .then(task => {
            document.getElementById('taskId').value = task.id;
            document.getElementById('taskTitle').value = task.title;
            document.getElementById('taskDescription').value = task.description;
            document.getElementById('taskDueDate').value = task.dueDate;
            document.getElementById('taskStatus').value = task.status;

            // Switch to edit mode
            document.getElementById('addTaskBtn').style.display = 'none';
            document.getElementById('editTaskBtn').style.display = 'inline';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to load task for editing.');
        });
}

// Save the edited task
function saveTask() {
    const taskId = document.getElementById('taskId').value;
    const updatedTask = {
        id: taskId,
        title: document.getElementById('taskTitle').value.trim(),
        description: document.getElementById('taskDescription').value.trim(),
        dueDate: document.getElementById('taskDueDate').value,
        status: document.getElementById('taskStatus').value
    };

    // Validate data
    if (!updatedTask.title || !updatedTask.description || !updatedTask.dueDate) {
        alert('All fields are required.');
        return;
    }

    // Simulate API call to save updated task
    fetch(`/api/tasks/${taskId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedTask)
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to save task');
            alert('Task updated successfully.');
            document.getElementById('taskForm').reset(); // Reset form
            document.getElementById('addTaskBtn').style.display = 'inline';
            document.getElementById('editTaskBtn').style.display = 'none';
            fetchTasks(); // Reload tasks
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to update task.');
        });
}

// Add a new task
function addTask() {
    const title = document.getElementById('taskTitle').value.trim();
    const description = document.getElementById('taskDescription').value.trim();
    const dueDate = document.getElementById('taskDueDate').value;

    // Validate input
    if (!title || !description || !dueDate) {
        alert('All fields are required.');
        return;
    }

    const newTask = {
        title: title,
        description: description,
        status: 'PENDING', // Default status is PENDING
        dueDate: dueDate
    };

    // Simulate API call to add a task
    fetch('/api/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newTask)
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to add task');
            return response.json();
        })
        .then(addedTask => {
            addTaskToList(addedTask);

            // Reset form
            document.getElementById('taskForm').reset();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to add task.');
        });
}

// Smooth animation when adding tasks to the list
function addTaskToList(task) {
    const taskList = document.getElementById('taskList');
    const listItem = document.createElement('li');
    listItem.classList.add('task-item');

    listItem.innerHTML = `
        <div class="task-content">
            <span class="task-id"><strong>ID:</strong> ${task.id}</span>
            <h3 class="task-title">${task.title}</h3>
            <p class="task-description"><strong>Description:</strong> ${task.description}</p>
            <p class="task-status ${task.status}"><strong>Status:</strong> ${task.status}</p>
            <p class="task-due-date"><strong>Due Date:</strong> ${task.dueDate}</p>
        </div>
        <div class="task-actions">
            <button class="edit-btn" onclick="editTask('${task.id}')">Edit</button>
            <button class="delete-btn" onclick="deleteTask('${task.id}')">Delete</button>
        </div>
    `;

    // Fade-in effect for new tasks
    listItem.style.opacity = '0';
    taskList.appendChild(listItem);

    setTimeout(() => {
        listItem.style.opacity = '1';
    }, 100);
}


// Set the current date by default in the due date input
window.onload = function() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // Months are zero-based
    const day = String(today.getDate()).padStart(2, '0');

    // Format date as YYYY-MM-DD
    const formattedDate = `${year}-${month}-${day}`;

    // Set the default value for the due date input
    document.getElementById('taskDueDate').value = formattedDate;
};

// Fetch tasks when the page loads
document.addEventListener('DOMContentLoaded', fetchTasks);
