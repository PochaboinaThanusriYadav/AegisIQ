from pathlib import Path

import joblib
from fastapi import FastAPI
from pydantic import BaseModel, Field


BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "model.pkl"

app = FastAPI(title="AegisIQ AI Service", version="1.0.0")


class PredictRequest(BaseModel):
    description: str = Field(..., min_length=1)
    location: str | None = None
    imageUrl: str | None = None


def load_model():
    if not MODEL_PATH.exists():
        return None
    return joblib.load(MODEL_PATH)


MODEL = load_model()


@app.get("/health")
def health():
    return {"status": "ok", "modelLoaded": MODEL is not None}


@app.post("/predict")
def predict(payload: PredictRequest):
    if MODEL is None:
        return {
            "severity": "MEDIUM",
            "confidence": 0.5,
            "priorityClassification": "MEDIUM",
            "recommendedAction": "Review the incident manually because the model is not trained yet.",
            "summary": "Model not trained. Returning a safe default response.",
            "category": "Unknown",
        }

    predicted_class = MODEL.predict([payload.description])[0]
    probabilities = MODEL.predict_proba([payload.description])[0]
    confidence = float(max(probabilities))

    severity = str(predicted_class)
    priority = "HIGH" if severity == "CRITICAL" else severity

    category = "General"
    lowered = payload.description.lower()
    if any(keyword in lowered for keyword in ["fire", "smoke", "gas", "explosion"]):
        category = "Fire"
    elif any(keyword in lowered for keyword in ["flood", "rain", "water"]):
        category = "Flood"
    elif any(keyword in lowered for keyword in ["accident", "collision", "crash"]):
        category = "Accident"
    elif any(keyword in lowered for keyword in ["weapon", "fight", "attack", "violence"]):
        category = "Security"

    recommended_action = {
        "CRITICAL": "Dispatch emergency responders immediately.",
        "HIGH": "Escalate to incident command and dispatch security.",
        "MEDIUM": "Review promptly and assign to the correct team.",
        "LOW": "Record for follow-up and monitoring.",
    }.get(severity, "Review the incident manually.")

    return {
        "severity": severity,
        "confidence": confidence,
        "priorityClassification": priority,
        "recommendedAction": recommended_action,
        "summary": f"Predicted {severity} severity using a TF-IDF logistic regression model.",
        "category": category,
    }