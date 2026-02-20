document.addEventListener('DOMContentLoaded', function () {

    function toggleSchedule(dayId) {
        const schedules = document.querySelectorAll('.day-schedule');
        const clicked = document.getElementById(dayId);

        schedules.forEach(function (s) {
            if (s !== clicked) s.style.display = 'none';
        });

        clicked.style.display = (clicked.style.display === 'none' || clicked.style.display === '') ? 'block' : 'none';
    }

    document.querySelectorAll('.day-buttons button').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const dayId = btn.getAttribute('data-day');
            if (dayId) toggleSchedule(dayId);
        });
    });

    document.addEventListener('click', function (event) {
        if (!event.target.closest('.day-buttons') && !event.target.closest('.day-schedule')) {
            document.querySelectorAll('.day-schedule').forEach(function (s) {
                s.style.display = 'none';
            });
        }
    });

    // Auto-expand today's schedule
    const days = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];
    const today = days[new Date().getDay()];
    const todayEl = document.getElementById(today);
    if (todayEl) todayEl.style.display = 'block';

});
