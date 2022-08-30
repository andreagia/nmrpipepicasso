import React, {useContext, useEffect, useState} from "react";
import http from "../http-common";
import {ContextInfo} from "../contexts/ContextInfo";

import ImageSSE from "./ImageSSE";

export default function RunCommandSSE(){

    const value = useContext(ContextInfo);
    const [listening, setListening] = useState(false);
    const [values, setValues] = useState({
        firstName: '',
        lastName: '',
        email: '',
        sessid: value.dirtmp,
        run: 'run'
    });
    console.log('Sono in RuncommandSSE');
    console.log(value.dirtmp);
    useEffect(() => {
        if (listening){
            console.log("PIPPO ", values);
            const postdata = {
                firstName: values.firstName,
                lastName: values.lastName,
                email: values.email,
                dirtmp: values.sessid,
                run: values.run
            };
            const postdata1 = {
                firstName: "values.firstName",
                lastName: "values.lastName",
                email: "values.email",
                dirtmp: "values.sessid",
                run: "values.run"
            };
            console.log("POSTDATA ", postdata1);
            http.post('/run', postdata).then((res) => {
                    console.log("RESPONSE RECEIVED: ", res);
                }
            );
            //http.get('/run', {params:{sessid: value.dirtmp, run: 'run'}});

        }
    })
    const handleFirstNameInputChange = (event) => {
        event.persist();
        console.log(event);
        setValues((values) => ({
            ...values,
            firstName: event.target.value,
        }));
    };
    const handleLastNameInputChange = (event) => {
        event.persist();
        setValues((values) => ({
            ...values,
            lastName: event.target.value,
        }));
    };

    const handleEmailInputChange = (event) => {
        event.persist();
        setValues((values) => ({
            ...values,
            email: event.target.value,
        }));
    };

    return (
        <div>
            <input
                id='first-name'
                className='form-field'
                type='text'
                placeholder='First Name'
                name='firstName'
                value={values.firstName}
                onChange={handleFirstNameInputChange}/>

            <input
                id="last-name"
                className="form-field"
                type="text"
                placeholder="Last Name"
                name="lastName"
                value={values.lastName}
                onChange={handleLastNameInputChange}
            />
            <input
                id="email"
                className="form-field"
                type="text"
                placeholder="Email"
                name="email"
                value={values.email}
                onChange={handleEmailInputChange}
            />

            <button onClick={() => setListening(true)}>
                Submit
            </button>
            {listening ? <button>Logout</button> : <button>Login</button>}
            { listening ? <ImageSSE dirtmp={value.dirtmp} /> : '' }
        </div>
    );
};