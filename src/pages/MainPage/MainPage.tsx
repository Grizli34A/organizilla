import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import useAllBoards from '../../hooks/useAllBoards';
import Layout from '../../components/Layout/Layout';
import CurrentDate from '../../components/CurrentDate/CurrentDate';
import SuccessRegister from '../../components/UI/Modals/SuccessRegister/SuccessRegisterModal';
import WorkSpaces from '../../components/WorkSpaces/WorkSpaces';
import { useSuccessRegisterStore } from '../../store';
import { ISortBoards } from '../../types/entityTypes';
import styles from './MainPage.module.scss';

const MainPage = () => {
    const { t } = useTranslation();
    const successRegisterVisible = useSuccessRegisterStore(state => state.successRegisterVisible);
    const [boards, setBoards] = useState<ISortBoards[]>([]);
    const { data, isSuccess } = useAllBoards();

    useEffect(() => {
        if (data) {
            setBoards(data.data);
        }
    }, [data]);

    return (
        <>
            <Layout>
                <CurrentDate />

                {isSuccess && boards.length > 0 && (
                    <>
                        <div className={styles.workSpaces}>{t('main.recentBoard')}</div>
                        <WorkSpaces boards={boards} isRecent={true} />
                    </>
                )}
                {isSuccess && boards.length > 0 && (
                    <>
                        <div className={styles.workSpaces}>{t('main.allBoards')}</div>
                        <WorkSpaces boards={data?.data} isRecent={false} />
                    </>
                )}

                {successRegisterVisible && <SuccessRegister />}
            </Layout>
        </>
    );
};

export default MainPage;
