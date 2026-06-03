from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from contextlib import asynccontextmanager
from typing import List
from fastapi_cache import FastAPICache
from fastapi_cache.backends.inmemory import InMemoryBackend
from fastapi_cache.decorator import cache

from app.database import engine, Base, get_db
from app import crud, schemas

@asynccontextmanager
async def lifespan(app: FastAPI):
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    FastAPICache.init(InMemoryBackend(), prefix="university-cache")
    yield

app = FastAPI(title="University Management API", lifespan=lifespan)

@app.get("/students/", response_model=List[schemas.StudentResponse])
async def read_students(db: AsyncSession = Depends(get_db)):
    return await crud.get_students(db)

@app.post("/students/", response_model=schemas.StudentResponse)
async def create_new_student(student: schemas.StudentCreate, db: AsyncSession = Depends(get_db)):
    return await crud.create_student(db, student)

@app.delete("/students/{student_id}")
async def delete_student_endpoint(student_id: int, db: AsyncSession = Depends(get_db)):
    success = await crud.safe_delete_student(db, student_id)
    if not success:
        raise HTTPException(status_code=404, detail="Student not found")
    return {"message": f"Student with ID {student_id} successfully deleted via transaction."}

@app.get("/courses/", response_model=List[schemas.CourseResponse])
@cache(expire=60)  
async def read_courses(db: AsyncSession = Depends(get_db)):
    courses = await crud.get_courses(db)
    return [schemas.CourseResponse.model_validate(c) for c in courses]

@app.post("/courses/", response_model=schemas.CourseResponse)
async def create_new_course(course: schemas.CourseCreate, db: AsyncSession = Depends(get_db)):
    return await crud.create_course(db, course)

@app.get("/grades/", response_model=List[schemas.GradeResponse])
@cache(expire=30) 
async def read_grades(db: AsyncSession = Depends(get_db)):
    grades = await crud.get_grades(db)
    return [schemas.GradeResponse.model_validate(g) for g in grades]

@app.post("/grades/", response_model=schemas.GradeResponse)
async def add_new_grade(grade: schemas.GradeCreate, db: AsyncSession = Depends(get_db)):
    return await crud.add_grade(db, grade)