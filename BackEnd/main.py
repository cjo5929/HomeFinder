from flask import Flask, request, jsonify
import joblib
import pandas as pd

app = Flask(__name__)

# 모델 불러오기
rf_model = joblib.load('rf_model.pkl')

@app.route('/predict', methods=['POST'])
def predict():
    # 요청 데이터 받기
    data = request.get_json()

    # 데이터 전처리
    age_30 = 1 if 30 <= data['age'] < 40 else 0
    age_40 = 1 if 40 <= data['age'] < 50 else 0
    age_50 = 1 if 50 <= data['age'] < 60 else 0
    age_60 = 1 if data['age'] >= 60 else 0
    reside_senm_x = 1 if data['reside_area'] == '해당지역' else 0
    avrg_score = data['score']
    lwet_score = data['score'] * 0.9
    top_score = data['score'] * 1.1

    # 입력 데이터 생성
    input_data = pd.DataFrame({
        'AGE_30': [age_30],
        'AGE_40': [age_40],
        'AGE_50': [age_50],
        'AGE_60': [age_60],
        'RESIDE_SENM_x': [reside_senm_x],
        'AVRG_SCORE': [avrg_score],
        'LWET_SCORE': [lwet_score],
        'TOP_SCORE': [top_score]
    })

    # 예측 수행
    win_prob = rf_model.predict(input_data)[0]

    # 당첨 확률에 따른 분류
    if win_prob < 30:
        win_rate = '낮음'
    elif win_prob < 60:
        win_rate = '보통'
    else:
        win_rate = '높음'

    # 결과 반환
    result = {
        'win_prob': round(win_prob, 2),
        'win_rate': win_rate
    }
    return jsonify(result)

if __name__ == '__main__':
    app.run()


