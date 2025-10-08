import http from 'k6/http';
import { check, sleep } from 'k6';

// 실제 DB에 저장된 member UUID 리스트 (100개)
const memberIds = [
    '0095e516-727c-47be-9d00-1295b3a415f2',
    '0507dfb7-76be-4e44-a303-c6eab4f6fe88',
    '0587d4b9-221c-46c6-a916-1499625cde81',
    '064c012f-e0f5-44ec-9bb0-5bf7996cb46e',
    '06bdac2c-e1c9-454e-976b-571be63e9d82',
    '06dc7dfb-d3ba-470e-8996-578bd3bac79e',
    '09ae2dc9-f828-4a9e-b0ae-acde591aa7ec',
    '09b9b465-9963-4a80-88a2-029326ac57e7',
    '0e1bb19f-c873-489e-b3eb-9de9c2122649',
    '0f1767c5-2bdd-4c18-9731-6fa03f5a235b',
    '138be42a-4627-4490-b19e-b1b099265ead',
    '142a65d6-f0bd-4e3f-9f60-bab79845fbdc',
    '19bd7938-11fe-4f7a-8cdb-338b90cd77c6',
    '1a792195-8267-4ec3-ac6d-518228ee473d',
    '1a8456ff-3b1e-42ff-b797-7ac88f74ba40',
    '1b269b3e-af0e-4993-988f-4cf7b653842d',
    '1f5ea41f-55ce-43b3-aa2b-aeb3e0f2ccc7',
    '2069ae3e-8db0-4ad2-afa4-ecc196d5a37a',
    '22ed50c1-acbd-4e76-9dbc-219b4261bea8',
    '2783c07c-edca-4651-9fd0-5a36abc4d20c',
    '2854aa4a-81d7-4f82-a911-6ff5b793c068',
    '2b988247-3683-4658-88aa-fb2fe720abce',
    '2c5f6471-811e-4141-83de-d4cfca7218dd',
    '31da32eb-05e0-4331-ad51-341c3e4aa3ca',
    '3219109a-b55d-4446-a7f3-59d773e7ab9c',
    '34686fb3-1298-4bd3-a45f-715156ee2432',
    '39c212e4-a440-437e-9cf5-59a2902fd531',
    '3d2a7331-5644-4620-a39f-73f609a7ae84',
    '3e8ab808-df44-480b-8279-ac4ba8633323',
    '40d19aba-e017-41c6-b103-9e2cbb57ce96',
    '4592b54f-0c25-40f8-b036-2981441d6a5d',
    '4ea7b131-ae65-4cf5-8e31-8450033114df',
    '517b4822-8d64-48af-a48a-b9628adae843',
    '528df64d-1467-4363-8544-08d0e4f272ec',
    '549087f6-468d-4823-ada8-103de8e1fe05',
    '5c6a176c-9ff2-414f-a944-be41e95dda8b',
    '5dee89e6-6bea-4b99-981a-810ccb286189',
    '5e7d1238-678a-4ccb-9dc3-4ca1554af643',
    '5f5470e8-ad31-42fb-94ff-400564928f11',
    '614f2c78-b47a-473b-8bfd-ba32c2233c0f',
    '62420412-da5d-4315-b316-cc39b26b8df9',
    '654d04cf-fadc-4228-b7da-f0c48955032b',
    '6796e577-cdb2-476f-be10-865e3ec6561c',
    '68c19ec5-d9cf-47ea-b89d-cb7700d8da9a',
    '7097e365-92d8-4ade-aa2b-958a64cf52e9',
    '73158885-0e2e-4e11-9d5d-6b0e3d31cae3',
    '74a76fa5-67e7-4908-9e3d-2d287adaa5a0',
    '76918090-6f61-4e63-988e-b54e83b05f76',
    '775f3c2f-72a6-4796-910a-1bd47507170e',
    '7d3ffb6a-466b-4104-ac8b-6ec4ea043e90',
    '844c0c3c-a9fe-4608-8601-f6549978620c',
    '84a7515f-675e-427f-bfa1-b5ae711ac2e2',
    '8bddee9c-ebf7-4dba-ae56-c0003c3cae63',
    '8e254046-1005-4ac3-a0a7-2684b5bec56b',
    '91350f96-6ca3-405c-ba8c-c2ad26d05903',
    '92733337-b25f-4c66-9c71-1c40057dfb47',
    '93aeb9f4-361e-4538-a64a-fc2fba1b3543',
    '9572914b-cb5e-452f-8ff5-ae45ae7808f0',
    '95a398d5-ef45-4e88-888c-59826d190112',
    '9c035e6e-8864-440d-9a5b-bd7a102b1842',
    '9e36387c-79c8-435e-b89f-10549c5046a7',
    'a02fc843-03a8-44f6-975a-aae996246dec',
    'a2f0a597-882b-4fdb-9a3b-80b3fb87cfc9',
    'a3418d3d-ab40-4c23-addf-714c496d9b4e',
    'a43ef672-4341-48fa-8065-0c3762486cce',
    'a4dcdbba-09e2-42b3-98bf-cf63ed6362cd',
    'a52adf5d-0a02-418a-afa5-d459a7982d33',
    'a64ad5bb-82dd-49eb-9631-c53cda1b290c',
    'a6ca1947-264a-45e0-be9f-c547b8465e58',
    'a793b61c-2f5a-46b5-9cd5-c53c4ab7bc68',
    'aa7e3833-ffe7-4662-b1bd-a9e5e18f0ec9',
    'afbbc77c-c343-4ccb-bfc4-f11fa72c47cc',
    'b0274faf-ad7b-413c-8244-9f365b06951a',
    'b03dff6c-68b8-44fd-9f90-cfb37686e1be',
    'b05fd76c-fd17-49b1-8d2b-752470473cca',
    'b07b517f-fef3-48c9-b3ca-e000b6aa6e00',
    'b756253e-9173-4393-a3da-65916c249b2f',
    'b8f65baf-131f-4619-a164-8bb0fa93a913',
    'bb0257a3-bde7-45a0-a319-7ed95a1ca123',
    'c15b1b9a-02c5-4894-8490-35a94661b849',
    'c252ec6c-24c4-4056-a497-03ee2744e354',
    'c2977c62-13fa-4d2a-860a-859c83a66919',
    'c41a559c-cc89-4128-a3e9-88aafd4fb60d',
    'c865ef5c-61da-47cf-9815-b0579cd0a777',
    'c86f78e8-4d16-44d4-b0cd-618a5a1f30e7',
    'cfa1d854-f432-4639-9e08-745356a2f2bd',
    'd22b6be5-d477-4121-bb3f-44c377689e44',
    'd25372cd-c51b-4c97-82fc-6909013c78de',
    'd7c68ccd-54a8-4c25-8d6e-a94ba10d9f5e',
    'd80c4073-a360-4b98-ad04-a2482d4bc67b',
    'd8e18c59-2217-47cc-9c76-f42def44f8cc',
    'd9552e18-4cc7-4e01-a774-34775fc94d4f',
    'e14b5b97-3aa7-4f52-b111-f7ca534b2660',
    'e1c900d7-312d-4110-859e-d88c7059bfc3',
    'e4105b80-79d8-4c1d-8886-a88357244a41',
    'e695b54e-8313-4b30-8fdf-d58c743c9c4c',
    'e9fd48f7-20b6-4e91-b44b-000af887e9b2',
    'e9fff55a-560f-4354-96f4-b556d7749c93',
    'f128983b-3056-4344-bf36-e909f70394b3',
    'f1b1b843-e79d-4062-9c4c-3c4a35e4575b',
];

export const options = {
    vus: 100, // 동시 접속 사용자 수
    duration: '30s', // 테스트 시간
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% 요청이 0.5초 이내에 끝나야 함
    },
};

export default function () {
    // 각 가상 유저가 랜덤 memberId로 요청
    const memberId = memberIds[Math.floor(Math.random() * memberIds.length)];
    const url = `http://localhost:8080/api/bus?memberId=${memberId}`;

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}
