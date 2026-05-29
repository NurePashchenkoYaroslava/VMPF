const express = require('express');
const cors = require('cors');
const nodemailer = require('nodemailer'); 
const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

const users = [
    { username: 'admin', password: '123', role: 'admin' },
    { username: 'user1', password: '123', role: 'user' }
];

let tasks = [
    { id: 1, title: 'Підготувати презентацію для курсової роботи', description: 'Доробити 8 слайдів презентації', completed: false, dueDate: '2026-06-01' },
    { id: 2, title: 'Купити книгу Мовчазна пацієнтка', description: 'Купити книгу подрузі', completed: false, dueDate: '2026-05-28' } 
];

function checkRole(allowedRoles) {
    return (req, res, next) => {
        const userRole = req.headers['x-user-role'];
        if (!userRole || !allowedRoles.includes(userRole)) {
            return res.status(403).json({ message: 'Доступ заборонено' });
        }
        next();
    };
}

app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    const user = users.find(u => u.username === username && u.password === password);
    if (!user) return res.status(401).json({ message: 'Невірний логін або пароль' });
    res.json({ username: user.username, role: user.role });
});

app.get('/api/tasks', (req, res) => {
    res.json(tasks);
});

app.get('/api/tasks/:id', (req, res) => {
    const task = tasks.find(t => t.id === parseInt(req.params.id));
    if (!task) return res.status(404).json({ message: 'Задачу не знайдено' });
    res.json(task);
});

app.post('/api/tasks', checkRole(['admin', 'user']), (req, res) => {
    const { title, description, dueDate } = req.body;
    const newTask = {
        id: tasks.length ? tasks[tasks.length - 1].id + 1 : 1,
        title,
        description,
        completed: false,
        dueDate: dueDate || null
    };
    tasks.push(newTask);
    res.status(201).json(newTask);
});

app.put('/api/tasks/:id', checkRole(['admin', 'user']), (req, res) => {
    const task = tasks.find(t => t.id === parseInt(req.params.id));
    if (!task) return res.status(404).json({ message: 'Задачу не знайдено' });

    task.title = req.body.title || task.title;
    task.description = req.body.description || task.description;
    task.completed = req.body.completed !== undefined ? req.body.completed : task.completed;
    task.dueDate = req.body.dueDate || task.dueDate;

    res.json(task);
});

app.delete('/api/tasks/:id', checkRole(['admin']), (req, res) => {
    tasks = tasks.filter(t => t.id !== parseInt(req.params.id));
    res.status(204).send();
});

app.get('/api/analytics', (req, res) => {
    const total = tasks.length;
    const completed = tasks.filter(t => t.completed).length;
    const pending = total - completed;
    const overdue = tasks.filter(t => !t.completed && t.dueDate && t.dueDate < new Date().toISOString().split('T')[0]).length;

    res.json({ total, completed, pending, overdue });
});

const transporter = nodemailer.createTransport({
    host: 'smtp.ethereal.email',
    port: 587,
    auth: {
        user: 'marcelle.zboncak@ethereal.email',
        pass: 'yRj6h4DkdFgU45ZFqg'
    }
});

app.post('/api/send-reminders', (req, res) => {
    const { username, email } = req.body; 
    
    if (!email) {
        return res.status(400).json({ message: 'Будь ласка, вкажіть email для відправки!' });
    }

    const overdueTasks = tasks.filter(t => !t.completed && t.dueDate && t.dueDate < new Date().toISOString().split('T')[0]);
    
    if (overdueTasks.length === 0) {
        return res.status(200).json({ message: 'У вас немає протермінованих задач, лист не потрібен!' });
    }

    let emailContent = `<h3>Привіт, ${username}!</h3><p>У вас є протерміновані задачі:</p><ul>`;
    overdueTasks.forEach(t => {
        emailContent += `<li><b>${t.title}</b> (Дедлайн: ${t.dueDate})</li>`;
    });
    emailContent += `</ul>`;

    const mailOptions = {
        from: 'tvoja.poshta@gmail.com', 
        to: email,                      
        subject: 'Нагадування про задачі (Task Manager)',
        html: emailContent
    };

    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            console.error(error);
            return res.status(500).json({ message: 'Помилка при відправці листа. Перевірте налаштування nodemailer.' });
        }
        res.status(200).json({ message: `Нагадування успішно відправлено на ${email}!` });
    });
});

app.listen(PORT, () => {
    console.log(`Сервер працює на http://localhost:${PORT}`);
});