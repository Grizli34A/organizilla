import { FC, useEffect, useState } from 'react';
import useListName from '../../hooks/useListName';
import ListContainer from '../ListContainer/ListContainer';
import styles from './List.module.scss';
import { IList } from '../../types/entityTypes';
import more from '../../assets/icons/more.svg';
import ListActions from '../ListActions/ListActions';
import { useShowActionStore } from '../../store';
import CreateCardButton from '../CreateCard/CreateCardButton.tsx/CreateCardButton';

interface IListProps {
    list: IList;
    boardId: string;
}

const List: FC<IListProps> = ({ list, boardId }) => {
    const showListActions = useShowActionStore(state => state.showListActions);
    const setShowListActions = useShowActionStore(state => state.setShowListActions);
    const [listName, setListName] = useState(list.name);
    const [isEditing, setIsEditing] = useState(false);
    const [rows, setRows] = useState(1);
    const { mutate } = useListName();

    useEffect(() => {
        const numLines = Math.min(Math.ceil(listName.length / 18), 10);
        setRows(numLines);
    }, [listName]);

    const handleNameChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setListName(e.target.value);
    };

    const handleBlur = () => {
        setIsEditing(false);
        mutate({ id: list.id, name: listName });
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter') {
            handleBlur();
        }
    };
    return (
        <div className={styles.listContainer}>
            <div className={styles.list}>
                <div className={styles.list__title}>
                    {isEditing ? (
                        <textarea
                            value={listName}
                            className={styles.list__title_input}
                            onChange={handleNameChange}
                            onBlur={handleBlur}
                            onKeyDown={handleKeyDown}
                            autoFocus
                            wrap="soft"
                            rows={rows}
                        />
                    ) : (
                        <>
                            <h2 onClick={() => setIsEditing(true)}>{listName}</h2>
                        </>
                    )}
                    <img src={more} width={20} onClick={() => setShowListActions(list.id)} />
                </div>
                <ListContainer cards={list.cards} listId={list.id.toString()} />
                <CreateCardButton listId={list.id} boardId={boardId} />
            </div>
            {showListActions === list.id && <ListActions id={list.id} boardId={boardId} />}
        </div>
    );
};

export default List;
