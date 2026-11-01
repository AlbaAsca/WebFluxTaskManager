// Cliente de la API: navega por los enlaces HATEOAS (_links) en vez de construir URLs a mano,
// y escucha /api/tasks/events (SSE) para reflejar al instante los cambios de cualquier usuario.

const API = '/api/tasks';
const STATUS_LABEL = { PENDING: 'Pendiente', IN_PROGRESS: 'En curso', COMPLETED: 'Hecha' };
let currentFilter = '';

const $ = (id) => document.getElementById(id);

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text ?? '';
    return div.innerHTML;
}

function toast(message) {
    const el = $('toast');
    el.textContent = message;
    el.classList.add('show');
    clearTimeout(toast.timer);
    toast.timer = setTimeout(() => el.classList.remove('show'), 2500);
}

async function api(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: { 'Content-Type': 'application/json', Accept: 'application/hal+json', ...options.headers },
    });
    if (response.status === 403) throw new Error('No tienes permiso: solo admin puede borrar tareas.');
    if (!response.ok) {
        const problem = await response.json().catch(() => ({}));
        const fields = problem.errors ? Object.values(problem.errors).join(' · ') : '';
        throw new Error(fields || problem.detail || `Error ${response.status}`);
    }
    return response.status === 204 ? null : response.json();
}

async function loadTasks() {
    const url = currentFilter ? `${API}?status=${currentFilter}` : API;
    const page = await api(url);
    renderTasks(page._embedded?.tasks ?? []);
}

function renderTasks(tasks) {
    const list = $('taskList');
    if (tasks.length === 0) {
        list.innerHTML = '<li class="empty">No hay tareas.</li>';
        return;
    }
    list.innerHTML = tasks.map((task) => `
        <li class="task-item" data-id="${escapeHtml(task.id)}">
            <div class="task-content">
                <h3 class="task-title">${escapeHtml(task.title)}</h3>
                ${task.description ? `<p class="task-description">${escapeHtml(task.description)}</p>` : ''}
                <p class="meta">
                    <span class="task-status ${task.status}">${STATUS_LABEL[task.status]}</span>
                    <span class="task-due-date">📅 ${escapeHtml(task.dueDate)}</span>
                </p>
            </div>
            <div class="task-actions">
                <button class="edit-btn" data-href="${task._links.update.href}">Editar</button>
                <button class="delete-btn" data-href="${task._links.delete.href}">Borrar</button>
            </div>
        </li>`).join('');
}

function resetForm() {
    $('taskForm').reset();
    $('editHref').value = '';
    $('taskDueDate').value = new Date().toISOString().slice(0, 10);
    $('submitBtn').textContent = 'Añadir tarea';
    $('cancelBtn').hidden = true;
    $('formError').textContent = '';
}

async function submitForm(event) {
    event.preventDefault();
    const body = JSON.stringify({
        title: $('taskTitle').value.trim(),
        description: $('taskDescription').value.trim() || null,
        status: $('taskStatus').value,
        dueDate: $('taskDueDate').value,
    });
    const editHref = $('editHref').value;
    try {
        await api(editHref || API, { method: editHref ? 'PUT' : 'POST', body });
        resetForm();
    } catch (error) {
        $('formError').textContent = error.message;
    }
}

async function startEdit(href) {
    const task = await api(href);
    $('editHref').value = task._links.update.href;
    $('taskTitle').value = task.title;
    $('taskDescription').value = task.description ?? '';
    $('taskDueDate').value = task.dueDate;
    $('taskStatus').value = task.status;
    $('submitBtn').textContent = 'Guardar cambios';
    $('cancelBtn').hidden = false;
    $('taskTitle').focus();
}

async function onListClick(event) {
    const button = event.target.closest('button');
    if (!button) return;
    try {
        if (button.classList.contains('edit-btn')) await startEdit(button.dataset.href);
        if (button.classList.contains('delete-btn') && confirm('¿Borrar esta tarea?')) {
            await api(button.dataset.href, { method: 'DELETE' });
        }
    } catch (error) {
        toast(error.message);
    }
}

function listenToChanges() {
    const source = new EventSource(`${API}/events`);
    const live = $('live');
    source.onopen = () => live.classList.replace('off', 'on');
    source.onerror = () => live.classList.replace('on', 'off'); // EventSource reintenta solo
    const labels = { CREATED: 'Nueva tarea', UPDATED: 'Tarea actualizada', DELETED: 'Tarea borrada' };
    for (const type of Object.keys(labels)) {
        source.addEventListener(type, (event) => {
            const change = JSON.parse(event.data);
            toast(`${labels[type]}${change.task ? `: ${change.task.title}` : ''}`);
            loadTasks().then(() => {
                document.querySelector(`[data-id="${CSS.escape(change.taskId)}"]`)?.classList.add('flash');
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    resetForm();
    $('taskForm').addEventListener('submit', submitForm);
    $('cancelBtn').addEventListener('click', resetForm);
    $('taskList').addEventListener('click', onListClick);
    document.querySelectorAll('.chip').forEach((chip) => chip.addEventListener('click', () => {
        document.querySelector('.chip.active').classList.remove('active');
        chip.classList.add('active');
        currentFilter = chip.dataset.status;
        loadTasks().catch((error) => toast(error.message));
    }));
    loadTasks().then(listenToChanges).catch((error) => toast(error.message));
});
