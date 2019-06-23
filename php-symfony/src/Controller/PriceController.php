<?php
declare(strict_types=1);

namespace App\Controller;

use DateTime;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

class PriceController
{
    /**
     * @Route(methods={"GET"}, path="/prices")
     */
    public function getPricesAction(Request $request): JsonResponse
    {
        $age = $request->query->get('age', null);

        $connection = $this->getConnection();
        $costStmt = $connection->prepare('SELECT cost FROM base_price WHERE type = :type');
        $costStmt->execute(['type' => $request->query->get('type')]);
        $results = $costStmt->fetchAll(\PDO::FETCH_ASSOC);
        $basePrice = null;
        foreach ($results as $result) {
            $basePrice = (int) $result['cost'];
        }

        if ($age !== null && $age < 6) {
            return new JsonResponse(['cost' => 0]);
        } else {
            if ($request->query->get('type') !== 'night') {
                $holidaysDates = [];

                $holidayStmt = $connection->prepare("SELECT * FROM holidays");
                $holidayStmt->execute();
                $holidays = $holidayStmt->fetchAll(\PDO::FETCH_ASSOC);

                foreach ($holidays as $holiday) {
                    $holidaysDates[] = \DateTime::createFromFormat('Y-m-d', $holiday['holiday']);
                }

                $reduction = 0;
                $isHoliday = false;
                foreach ($holidaysDates as $holiday) {
                    if ($request->query->get('date') !== null) {
                        $d = DateTime::createFromFormat('Y-m-d', $request->query->get('date'));
                        if ($d->format('Y') === $holiday->format('Y') &&
                            $d->format('m') === $holiday->format('m') &&
                            $d->format('d') === $holiday->format('d')
                        ) {
                            $isHoliday = true;
                        }
                    }
                }

                if ($request->query->get('date') !== null) {
                    $date = \DateTime::createFromFormat('Y-m-d', $request->query->get('date'));
                    $jd = gregoriantojd($date->format('m'), $date->format('d'), $date->format('d'));
                    if (!$isHoliday && jddayofweek($jd) == 'Monday') {
                        $reduction = 35;
                    }
                }

                if ($age !== null && $age < 15) {
                    return new JsonResponse(['cost' => ((int) ceil((int)$basePrice * .7))]);
                } else {
                    if ($age === null) {
                        $cost = $basePrice * (1 - $reduction / 100.0);

                        return new JsonResponse(['cost' => ((int) ceil($cost))]);
                    } else {
                        if ($age > 64) {
                            $cost = $basePrice * .75 * (1 - $reduction / 100.0);

                            return new JsonResponse(['cost' => ((int) ceil($cost))]);
                        } else {
                            $cost = $basePrice * (1 - $reduction / 100.0);
                            return new JsonResponse(['cost' => ((int) ceil($cost))]);
                        }
                    }
                }
            } else {
                if ($age !== null && $age >= 6) {
                    if ($age > 64) {
                        return new JsonResponse(['cost' => ((int) ceil($basePrice * .4))]);
                    } else {
                        return new JsonResponse(['cost' => $basePrice]);
                    }
                } else {
                    return new JsonResponse(['cost' => 0]);
                }
            }
        }
    }

    /**
     * @Route(methods={"PUT"}, path="/prices")
     */
    public function putPricesAction(Request $request): JsonResponse
    {
        $liftPassCost = (int) $request->query->get('cost');
        $liftPassType = $request->query->get('type');
        $connection = $this->getConnection();
        $stmt = $connection->prepare('INSERT INTO base_price (type, cost) VALUE (?, ?) ON DUPLICATE KEY UPDATE cost = ?');
        $stmt->execute([$liftPassType, $liftPassCost, $liftPassCost]);

        return new JsonResponse([]);
    }

    private function getConnection(): \PDO
    {
        $dsn = 'mysql:dbname=lift_pass;host=127.0.0.1';
        $user = 'root';
        $password = 'mysql';

        return new \PDO($dsn, $user, $password);
    }
}
