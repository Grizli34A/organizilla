import React from 'react';
import styles from './UserProfileSettings.module.scss';
import useUserData from '../../hooks/useUserData';
const UserProfileSettings = () => {
    const { data } = useUserData();
    return (
        <div className={styles.userProfileSettings}>
            <div className={styles.userProfileSettings__item}>
                <div className={styles.userProfileSettings__item_name}>
                    <h4>Имя</h4>
                    {localStorage.getItem('username')}
                </div>
                <button>Edit username</button>
            </div>
            <div className={styles.userProfileSettings__item}>
                <div className={styles.userProfileSettings__item_email}>
                    <h4>Электронная почта</h4>
                    {data && <p>{data.data}</p>}
                </div>

                <button>Edit email</button>
            </div>
            <div className={styles.userProfileSettings__item}>
                <div className={styles.userProfileSettings__item_password}>
                    <h4>Пароль</h4>
                </div>

                <button>Edit password</button>
            </div>
        </div>
    );
};

export default UserProfileSettings;
