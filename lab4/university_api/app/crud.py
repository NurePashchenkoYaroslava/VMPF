from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from app import models, schemas

async def get_students(db: AsyncSession):
    result = await db.execute(select(models.Student))
    return result.scalars().all()

async def create_student(db: AsyncSession, student: schemas.StudentCreate):
    db_student = models.Student(name=student.name, email=student.email)
    db.add(db_student)
    await db.commit()
    await db.refresh(db_student)
    return db_student

async def safe_delete_student(db: AsyncSession, student_id: int):
    async with db.begin():
        result = await db.execute(select(models.Student).where(models.Student.id == student_id))
        student = result.scalar_one_or_none()
        if student:
            await db.delete(student)
            return True
    return False

async def get_courses(db: AsyncSession):
    result = await db.execute(select(models.Course))
    return result.scalars().all()

async def create_course(db: AsyncSession, course: schemas.CourseCreate):
    db_course = models.Course(title=course.title, teacher_id=course.teacher_id)
    db.add(db_course)
    await db.commit()
    await db.refresh(db_course)
    return db_course

async def get_grades(db: AsyncSession):
    result = await db.execute(select(models.Grade))
    return result.scalars().all()

async def add_grade(db: AsyncSession, grade: schemas.GradeCreate):
    db_grade = models.Grade(student_id=grade.student_id, course_id=grade.course_id, score=grade.score)
    db.add(db_grade)
    await db.commit()
    await db.refresh(db_grade)
    return db_grade