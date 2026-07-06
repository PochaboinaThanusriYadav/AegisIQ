from pathlib import Path

import joblib
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline


BASE_DIR = Path(__file__).resolve().parent
DATASET_PATH = BASE_DIR / "dataset.csv"
MODEL_PATH = BASE_DIR / "model.pkl"


def main() -> None:
    df = pd.read_csv(DATASET_PATH)

    pipeline = Pipeline(
        [
            ("vectorizer", TfidfVectorizer(ngram_range=(1, 2), stop_words="english")),
            ("model", LogisticRegression(max_iter=1000)),
        ]
    )

    pipeline.fit(df["description"], df["severity"])
    joblib.dump(pipeline, MODEL_PATH)
    print(f"Saved model to {MODEL_PATH}")


if __name__ == "__main__":
    main()