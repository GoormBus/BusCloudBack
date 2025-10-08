import http from 'k6/http';
import { check, sleep } from 'k6';

// 실제 DB에 저장된 member UUID 리스트
const memberIds = [
    '0001293a-5358-4958-8373-aa8ec56bdeb7',
    '004a389b-bff9-4f8f-a159-d267e341a2fb',
    '056f5ac1-100b-4be5-88ce-f526e3bfd8a5',
    '0a4ca1ac-3d4f-410d-8d88-587975ea9e45',
    '0c97cba7-1f70-4eda-8a37-e7dd17b0aa87',
    '0f70b009-7d20-4734-be0f-dabad37f7c05',
    '159ff9be-67a1-455b-a841-efb1086bd4a3',
    '1817e7d6-a78c-499e-8182-a014f85b944d',
    '1853265c-03b0-4ff2-9850-09c755bec1e5',
    '1f9c4eba-5b43-4c7b-97d8-499fa411cfae',
    '278ea141-c320-40ac-a02c-9c4176349b85',
    '2aa9c92c-30a1-4f0b-9eb4-ebdbd988b74c',
    '2aae3f7c-81a4-4a28-bd8c-671047001b7e',
    '349f7c98-e11f-4669-8920-18e14badf191',
    '36f8867b-b06f-4052-bf6b-68243a83ddbe',
    '391d642b-2759-4c17-9a5d-72c4b626f5c0',
    '39e340f2-a128-4c03-bc44-627f224c1161',
    '4da24731-e401-4106-aad4-2a2b22762fa7',
    '521f5372-7ed0-407a-aade-bc6213e871e4',
    '54b8e881-3f87-4d47-a713-b9875f980c7e',
    '5bc211ce-0252-404f-ae6b-46ce100ecd2d',
    '69c7da1b-1aa8-4a96-92b4-600a4f17565f',
    '6a96b74d-0c4c-4303-be86-c6e2d173f8a5',
    '72f363ff-6918-46ac-a2d1-35c352fb3a12',
    '73f0105d-db42-4123-86ae-59e21cd035eb',
    '7f3736e9-7b47-4459-adf7-a1f42602db1f',
    '7fcd89cf-35b2-4fe0-b262-fbc02dc340a9',
    '9b6fe41b-aa8e-45e8-ba00-f812fb422412',
    'a8a9b893-8399-4868-b473-f4360258833e',
    'a9c0c1f3-60ef-4977-82c4-0e8c8ce5482d',
    'aab11d0b-0d60-40e7-9aea-1eb56e832634',
    'b4606d98-542a-45c9-ac7c-2e8fdb5f409e',
    'ba3a4f07-58bc-4803-8c24-ffa77f90316b',
    'ba717f0b-e13c-448a-bec4-9a20f503d4c9',
    'ba91632d-c569-4620-95ed-25bb93669339',
    'bbbcf292-cbab-4f5e-bcc5-d9e69c0d7515',
    'bbdc988e-8fa2-4277-9bca-30dc3fa068c1',
    'cb07c84e-da57-441b-8552-3d17a6ab6947',
    'ccb4f377-be34-4e7a-8914-f40d022d24d2',
    'df3c0f17-d71e-4f86-9580-13e975fa49d6',
    'df707b02-c5c4-4b86-a5ae-b6566c0daf77',
    'e0d67085-1add-4eb4-a0f1-1a887a897833',
    'e8132e59-a259-44f9-a74a-cfd3738a81cd',
    'ea1e80b9-714b-47c9-9fa7-bbbfa314e223',
    'eb4d6a9a-3425-4ca4-ac71-3267d89ea2bd',
    'eca40559-8e5a-4bc8-a1d3-69d90e4f122c',
    'f2a6fd17-a77d-4787-a26c-175a23f0c970',
    'f47b8b9b-16b3-4e89-b0ea-098813c4848f',
    'f58a6558-6c45-4ecc-8dde-31cfa41c0a74',
    'fe57b5ab-4070-43cd-b123-ec88f58de5f4',
];

export const options = {
    vus: 50, // 50명 동시 접속
    duration: '30s', // 30초 실행
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% 요청이 0.5초 이하
    },
};

export default function () {
    // 각 VU가 랜덤한 member UUID로 요청
    const memberId = memberIds[Math.floor(Math.random() * memberIds.length)];
    const url = `http://localhost:8080/api/bus?memberId=${memberId}`;

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}
